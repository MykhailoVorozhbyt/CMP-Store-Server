#!/usr/bin/env node
'use strict';

/**
 * Claude Code PreToolUse hook
 * Rule: no android.* in any module's src/commonMain/**
 *
 * Covers: Write, Edit, MultiEdit (new code only) + heuristic for Bash / PowerShell writes.
 * Decision: JSON permissionDecision "deny" -> tool call is cancelled,
 * the reason is sent back to Claude so it can fix the code itself.
 *
 * Env:
 *   KMP_HOOK_BLOCK_ANDROIDX=1  also block androidx.* (off by default: many androidx libs are KMP now)
 *   KMP_HOOK_FAIL_CLOSED=1     block the call if the hook itself fails (default: fail open)
 */

const path = require('node:path');

// ---------- config ----------

// Project-relative, '/'-separated paths. Every KMP module (shared, app/shared, core/*, component/*/*, feature/*).
const PROTECTED_SOURCE_SETS = [/(^|\/)src\/commonMain\//];
const KOTLIN_FILE = /\.(kt|kts)$/;

// Lookbehind rejects foo.android.x, myandroid.x and `android.x`; `android\.` never matches androidx.
const FORBIDDEN = [{ name: 'android.*', re: /(?<![\w.`])android\.[A-Za-z_]/ }];
if (process.env.KMP_HOOK_BLOCK_ANDROIDX === '1') {
  FORBIDDEN.push({ name: 'androidx.*', re: /(?<![\w.`])androidx\.[A-Za-z_]/ });
}

const FAIL_CLOSED = process.env.KMP_HOOK_FAIL_CLOSED === '1';

// ---------- io ----------

function readStdin() {
  return new Promise((resolve, reject) => {
    let data = '';
    process.stdin.setEncoding('utf8');
    process.stdin.on('data', (chunk) => (data += chunk));
    process.stdin.on('end', () => resolve(data));
    process.stdin.on('error', reject);
  });
}

function deny(reason) {
  process.stdout.write(
    JSON.stringify({
      hookSpecificOutput: {
        hookEventName: 'PreToolUse',
        permissionDecision: 'deny',
        permissionDecisionReason: reason,
      },
    })
  );
  process.exit(0);
}

function allow() {
  // No output + exit 0 = no opinion; normal permission flow continues.
  process.exit(0);
}

// ---------- path helpers ----------

function projectRoot(input) {
  return process.env.CLAUDE_PROJECT_DIR || input.cwd || process.cwd();
}

function toProjectRelative(filePath, input) {
  const root = projectRoot(input);
  const abs = path.resolve(root, filePath);
  return path.relative(root, abs).split(path.sep).join('/');
}

function isProtected(relPath) {
  return KOTLIN_FILE.test(relPath) && PROTECTED_SOURCE_SETS.some((re) => re.test(relPath));
}

// ---------- Kotlin-aware stripping ----------

/**
 * Replaces comments and string literal contents with spaces, keeping newlines,
 * so line numbers in the result match the original.
 * - Kotlin block comments nest: /* a /* b *\/ c *\/
 * - String templates ${...} are code and are kept.
 */
function stripCommentsAndStrings(src) {
  const n = src.length;
  const blank = (s) => s.replace(/[^\n]/g, ' ');
  let out = '';
  let i = 0;

  while (i < n) {
    const c = src[i];
    const c2 = src[i + 1];

    // line comment
    if (c === '/' && c2 === '/') {
      let j = src.indexOf('\n', i);
      if (j === -1) j = n;
      out += blank(src.slice(i, j));
      i = j;
      continue;
    }

    // block comment, nested
    if (c === '/' && c2 === '*') {
      let depth = 1;
      let j = i + 2;
      while (j < n && depth > 0) {
        if (src[j] === '/' && src[j + 1] === '*') { depth++; j += 2; }
        else if (src[j] === '*' && src[j + 1] === '/') { depth--; j += 2; }
        else j++;
      }
      out += blank(src.slice(i, j));
      i = j;
      continue;
    }

    // string or raw string
    if (c === '"') {
      const raw = src.startsWith('"""', i);
      const quote = raw ? '"""' : '"';
      out += ' '.repeat(quote.length);
      i += quote.length;

      while (i < n) {
        if (!raw && src[i] === '\\') {
          out += blank(src.slice(i, i + 2));
          i += 2;
          continue;
        }
        if (src.startsWith(quote, i)) {
          let j = i + quote.length;
          if (raw) while (j < n && src[j] === '"') j++; // """ab""""
          out += ' '.repeat(j - i);
          i = j;
          break;
        }
        if (!raw && src[i] === '\n') break; // unterminated single-line string
        if (src[i] === '$' && src[i + 1] === '{') {
          let depth = 1;
          let j = i + 2;
          while (j < n && depth > 0) {
            if (src[j] === '{') depth++;
            else if (src[j] === '}') depth--;
            j++;
          }
          const end = depth === 0 ? j - 1 : j;
          out += '  ' + src.slice(i + 2, end) + (depth === 0 ? ' ' : '');
          i = j;
          continue;
        }
        out += src[i] === '\n' ? '\n' : ' ';
        i++;
      }
      continue;
    }

    // char literal
    if (c === "'") {
      let j = i + 1;
      while (j < n && src[j] !== "'" && src[j] !== '\n') {
        if (src[j] === '\\') j++;
        j++;
      }
      j = Math.min(j + 1, n);
      out += blank(src.slice(i, j));
      i = j;
      continue;
    }

    out += c;
    i++;
  }
  return out;
}

function findViolations(code) {
  const stripped = stripCommentsAndStrings(code).split('\n');
  const original = code.split('\n');
  const hits = [];
  stripped.forEach((line, idx) => {
    for (const rule of FORBIDDEN) {
      if (rule.re.test(line)) {
        hits.push({ line: idx + 1, rule: rule.name, snippet: original[idx].trim().slice(0, 160) });
      }
    }
  });
  return hits;
}

// ---------- tool input -> new code fragments ----------

function fragmentsFor(toolName, ti) {
  switch (toolName) {
    case 'Write':
      return [{ where: 'content', text: ti.content ?? '' }];
    case 'Edit':
      return [{ where: 'new_string', text: ti.new_string ?? '' }];
    case 'MultiEdit':
      return (ti.edits ?? []).map((e, k) => ({ where: `edits[${k}].new_string`, text: e.new_string ?? '' }));
    default:
      return [];
  }
}

const FIX_HINT =
  'Fix: commonMain must stay platform-agnostic. Declare an `expect` API (or an interface) in commonMain ' +
  'and put the `actual` implementation using android.* into the same module\'s src/androidMain/. ' +
  'For Context-like dependencies, inject them from the platform side (DI) instead of referencing them here.';

function report(relPath, hits) {
  return [
    `Blocked by KMP rule: ${relPath} is in commonMain, android.* APIs are not allowed there.`,
    ...hits.map((h) => `  ${h.where}:${h.line} [${h.rule}] ${h.snippet}`),
    FIX_HINT,
  ].join('\n');
}

// ---------- Bash / PowerShell heuristic ----------

// Only commands that WRITE into commonMain: redirect, tee, sed -i. Reading (grep, cat) is never blocked.
// In shell text a path is preceded by space, quote or '=', not only '/'.
const BASH_PROTECTED = PROTECTED_SOURCE_SETS.map((re) => new RegExp(re.source.replace('(^|\\/)', '(^|[\\/\\s"\'=])')));

const BASH_WRITE = /(>>?\s*["']?[^\s"']*commonMain)|\btee\b[^|;&]*commonMain|\bsed\s+(-[a-zA-Z]*i|--in-place)/;
const POWERSHELL_WRITE = /\b(Set-Content|Add-Content|Out-File|New-Item|WriteAllText|WriteAllLines|AppendAllText)\b[^|;]*commonMain/i;

function checkShell(command) {
  const cmd = command.replace(/\\/g, '/');
  if (!BASH_PROTECTED.some((re) => re.test(cmd))) return null;
  if (!BASH_WRITE.test(cmd) && !POWERSHELL_WRITE.test(cmd)) return null;
  // Raw scan: Kotlin comment/string rules don't apply to shell text ('...' is a shell string, not a char).
  const original = command.split('\n');
  const hits = [];
  original.forEach((line, idx) => {
    for (const rule of FORBIDDEN) {
      if (rule.re.test(line)) hits.push({ where: 'command', line: idx + 1, rule: rule.name, snippet: line.trim().slice(0, 160) });
    }
  });
  return hits.length ? hits : null;
}

// ---------- main ----------

async function main() {
  const input = JSON.parse(await readStdin());
  const toolName = input.tool_name;
  const ti = input.tool_input ?? {};

  if (toolName === 'Bash' || toolName === 'PowerShell') {
    const hits = checkShell(ti.command ?? '');
    if (hits) deny(report(`${toolName} command writing to commonMain`, hits));
    return allow();
  }

  if (!ti.file_path) return allow();
  const rel = toProjectRelative(ti.file_path, input);
  if (!isProtected(rel)) return allow();

  const hits = fragmentsFor(toolName, ti).flatMap((f) =>
    findViolations(f.text).map((h) => ({ ...h, where: f.where }))
  );
  if (hits.length) deny(report(rel, hits));
  allow();
}

main().catch((err) => {
  process.stderr.write(`[no-android-in-common] hook error: ${err && err.stack ? err.stack : err}\n`);
  // exit 2 = block (stderr goes to Claude); exit 1 = non-blocking, shown to user only
  process.exit(FAIL_CLOSED ? 2 : 1);
});