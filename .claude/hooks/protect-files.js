#!/usr/bin/env node
'use strict';

const path = require('node:path');

const RULES = [
  {
    re: /(^|\/)build\//,
    decision: 'deny',
    reason: 'Generated Gradle output. Change the source and let the build regenerate it.',
  },
  {
    re: /(^|\/)\.(gradle|kotlin)\//,
    decision: 'deny',
    reason: 'Gradle / Kotlin cache. Never edit it by hand.',
  },
  {
    re: /^graphify-out\//,
    decision: 'deny',
    reason: 'Generated knowledge graph. Refresh it with `graphify update .` instead of editing it.',
  },
  {
    re: /(^|\/)store\.db(-journal|-wal|-shm)?$/,
    decision: 'deny',
    reason: 'Dev SQLite database. Change the tables in server/src/main/kotlin/org/cmp/store/database/tables; DatabaseFactory recreates the schema.',
  },
  {
    re: /\.pbxproj$/,
    decision: 'ask',
    reason: 'Xcode project file: fragile, merge-hostile and not built since the move to app/ (CLAUDE.md Known Gaps). The user must confirm this edit.',
  },
];

const SHELL_RULES = [
  {
    re: /secrets\.properties|DESKTOP_CLIENT_SECRET/i,
    decision: 'deny',
    reason:
      'Shell access to secrets.properties / DESKTOP_CLIENT_SECRET is blocked: the value must never enter the conversation. ' +
      'To find usages in code use the Grep tool (it skips gitignored files); if the secret itself is needed, ask the user.',
  },
];

const SHELL_TOOLS = new Set(['Bash', 'PowerShell']);

function readStdin() {
  return new Promise((resolve, reject) => {
    let data = '';
    process.stdin.setEncoding('utf8');
    process.stdin.on('data', (chunk) => (data += chunk));
    process.stdin.on('end', () => resolve(data));
    process.stdin.on('error', reject);
  });
}

function decide(decision, reason) {
  process.stdout.write(
    JSON.stringify({
      hookSpecificOutput: {
        hookEventName: 'PreToolUse',
        permissionDecision: decision,
        permissionDecisionReason: reason,
      },
    })
  );
  process.exit(0);
}

function projectRelative(filePath, input) {
  const root = process.env.CLAUDE_PROJECT_DIR || input.cwd || process.cwd();
  return path.relative(root, path.resolve(root, filePath)).split(path.sep).join('/');
}

async function main() {
  const input = JSON.parse(await readStdin());
  const ti = input.tool_input ?? {};

  if (SHELL_TOOLS.has(input.tool_name)) {
    const rule = SHELL_RULES.find((r) => r.re.test(ti.command ?? ''));
    if (rule) decide(rule.decision, rule.reason);
    process.exit(0);
  }

  const filePath = ti.file_path ?? ti.notebook_path;
  if (!filePath) process.exit(0);

  const rel = projectRelative(filePath, input);
  if (rel.startsWith('../') || path.isAbsolute(rel)) process.exit(0);

  const rule = RULES.find((r) => r.re.test(rel));
  if (rule) decide(rule.decision, `${rel}: ${rule.reason}`);
  process.exit(0);
}

main().catch((err) => {
  process.stderr.write(`[protect-files] hook error: ${err && err.stack ? err.stack : err}\n`);
  process.exit(1);
});
