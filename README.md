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
raw shell error rather than a useful message. Install these first:

| Tool | Why | Install (macOS) |
|---|---|---|
| `bb` (Babashka) | runs the whole toolchain and the tests | `brew install borkdude/brew/babashka` |
| `tmux` | each agent runs in its own tmux session | `brew install tmux` |
| `codex` | the agent backend configured in `swarmforge/swarmforge.conf` | see the Codex CLI docs |
| `zsh`, `curl`, `tar`, `git` | launcher and bootstrap | preinstalled on macOS |

Verify everything at once:

```bash
for c in zsh tmux bb codex curl tar git; do printf "%-8s " "$c"; command -v $c || echo MISSING; done
```

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
`chmod +x swarm`, or use `git clone` instead.

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
