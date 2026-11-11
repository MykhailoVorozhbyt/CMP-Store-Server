let input = "";
process.stdin.on("data", (chunk) => (input += chunk));
process.stdin.on("end", () => {
  const command = JSON.parse(input).tool_input?.command ?? "";
  const gitCommit = /\bgit(\.exe)?((\s+-[cC]\s+\S+)|(\s+--[\w-]+(=\S+)?))*\s+commit\b/i;
  if (gitCommit.test(command)) {
    process.stderr.write("git commit is blocked: only the user commits, manually. Do not retry or work around this; tell the user the changes are ready to commit.\n");
    process.exit(2);
  }
  process.exit(0);
});
