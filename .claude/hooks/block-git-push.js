let input = "";
process.stdin.on("data", (chunk) => (input += chunk));
process.stdin.on("end", () => {
  const command = JSON.parse(input).tool_input?.command ?? "";
  const gitPush = /\bgit(\.exe)?((\s+-[cC]\s+\S+)|(\s+--[\w-]+(=\S+)?))*\s+push\b/i;
  if (gitPush.test(command)) {
    process.stderr.write("git push is blocked: only the user pushes, manually. Do not retry or work around this; tell the user the changes are ready to push.\n");
    process.exit(2);
  }
  process.exit(0);
});
