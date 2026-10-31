## Code Style

**Default: no comments** — neither KDoc nor plain `//` lines. A comment is justified only when it explains *why*
and the code genuinely cannot express it; that should be rare. If code is unclear without a comment, fix the code:
rename the variable, extract a constant, or pull out a function with a telling name. "What happens here" comments
and KDoc that restates a signature drift away from the code during refactoring and turn into noise.

