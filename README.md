# swarmforge-internal-test

A local name-registration CRUD written in Babashka/Clojure, built and maintained
through a [SwarmForge](https://github.com/unclebob/swarm-forge) agent swarm.

- `specifications/` — Gherkin features and QA notes that drive the work
- `src/name_registration.clj` — screen state: add, edit, delete, validation, pluggable storage
- `test/` — unit suite and runner
- `acceptance/` — Gherkin runtime, step definitions, and the generator that turns parsed features into a runnable suite
- `swarmforge/` — role prompts and constitution for the agent swarm
- `swarm` — launcher for the swarm

## Prerequisites

`./swarm` has no dependency checks of its own, so a missing tool surfaces as a
raw shell error rather than a useful message. Install these first.

### macOS and Linux

| Tool | Why | Install (macOS) |
|---|---|---|
| `bb` (Babashka) | runs the whole toolchain and the tests | `brew install borkdude/brew/babashka` |
| `tmux` | each agent runs in its own tmux session | `brew install tmux` |
| `codex` | the agent backend configured in `swarmforge/swarmforge.conf` | see the Codex CLI docs |
| `zsh`, `curl`, `tar`, `git` | launcher and bootstrap | preinstalled on macOS |

On Linux, use your package manager and Babashka's install script — the snippet
in the Windows section below applies unchanged.

Verify everything at once:

```bash
for c in zsh tmux bb codex curl tar git; do printf "%-8s " "$c"; command -v $c || echo MISSING; done
```

### Windows

**The swarm does not run on native Windows.** `swarm` is a bash script, the
launcher it calls is `#!/usr/bin/env zsh`, and every agent runs in a `tmux`
session — none of which exist under PowerShell, `cmd`, or Git Bash. Run it
inside **WSL2** instead, which is the path SwarmForge itself targets: its
Windows Terminal adapter shells out to `wt.exe` and `wsl.exe -e bash`.

Set up a WSL2 distribution (Ubuntu is fine), then install the prerequisites
*inside* it — a tool installed on the Windows side is not visible to WSL:

```bash
sudo apt update && sudo apt install -y zsh tmux curl tar git
curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install
chmod +x install && sudo ./install        # babashka
# install the codex CLI inside WSL as well
```

Then verify and run exactly as on macOS, from a WSL shell:

```bash
for c in zsh tmux bb codex curl tar git; do printf "%-8s " "$c"; command -v $c || echo MISSING; done
git clone https://github.com/eduardocpinzon/swarmforge-internal-test.git
cd swarmforge-internal-test
./swarm
```

#### WSL gotchas

**Clone into the Linux filesystem** (`~/`), not `/mnt/c/...`. Windows drive
mounts do not preserve the Unix executable bit reliably, which turns
`./swarm` into `permission denied`, and they are considerably slower.

**Line endings must stay LF.** If the tree is checked out by Git for Windows
with `core.autocrlf=true`, the scripts get CRLF endings and fail with
`bad interpreter: /usr/bin/env bash^M`. Cloning with the Git *inside* WSL
avoids this; to fix an existing clone, run `git config core.autocrlf false`
and re-checkout.

**Terminal surfaces are optional here.** All six roles in
`swarmforge/swarmforge.conf` are declared `window-invisible`, so no terminal
windows are opened on any platform and you drive the swarm from the dashboard
URL that `./swarm` prints. If you switch roles to visible windows, SwarmForge
auto-selects its Windows Terminal backend when `wt.exe` is on the WSL `PATH`;
force the choice with `SWARMFORGE_TERMINAL=windows-terminal` (or `none`).

## Running the swarm

```bash
git clone https://github.com/eduardocpinzon/swarmforge-internal-test.git
cd swarmforge-internal-test
./swarm
```

### How the launcher works

`swarm` is a bootstrapper, not the program itself. `swarmforge/scripts/` is
deliberately excluded from version control (see `.gitignore`), so on a fresh
clone that directory **does not exist**. On first run `swarm` downloads it from
`unclebob/swarm-forge`, then hands off to `swarmforge/scripts/swarmforge.sh`,
which execs `bb swarmforge.bb`.

The full chain is therefore: **curl → tar → zsh → bb → tmux → codex**. The first
run needs network access; later runs reuse the downloaded scripts.

Two environment variables override the bootstrap source:

```bash
SWARMFORGE_SCRIPTS_BRANCH=main   # branch of the upstream scripts repo
SWARMFORGE_SCRIPTS_URL=...       # full tarball URL, overrides the branch
```

### Troubleshooting `./swarm`

**`bb: command not found`** (or `tmux`, or `codex`) — the prerequisite is
missing. Run the verification loop above and install what it reports.

**`permission denied: ./swarm`** — the repository was downloaded as a ZIP from
GitHub rather than cloned, which drops the executable bit. Fix with
`chmod +x swarm`, or use `git clone` instead. Under WSL the same symptom comes
from cloning onto a `/mnt/c` drive mount; clone into the Linux filesystem.

**The bootstrap download fails** — the machine has no network access, or
`github.com/unclebob/swarm-forge` is unreachable. The first run cannot proceed
without it; point `SWARMFORGE_SCRIPTS_URL` at a reachable mirror if needed.

**Stale sessions after a crash** — the tmux socket lives at
`/tmp/swarmforge-$USER/<hash>.sock`. Inspect with
`tmux -S /tmp/swarmforge-$USER/<hash>.sock list-sessions`.

## Tests

```bash
bb test:unit        # unit suite
bb test:acceptance  # Gherkin acceptance suite
bb test             # both
```

`bb test:unit` passes: 5 tests, 14 assertions.

`bb test:acceptance` currently **cannot run on a stock setup**, for two reasons
unrelated to the feature code:

1. it shells out to a `gherkin-parser` binary that is not part of this repo and
   is not installed by the bootstrap;
2. `acceptance/generate.clj` requires `babashka.json`, which is absent from some
   Babashka builds (confirmed missing on v1.13.219).

Both gaps have to be closed before the acceptance path can be considered
verified.
