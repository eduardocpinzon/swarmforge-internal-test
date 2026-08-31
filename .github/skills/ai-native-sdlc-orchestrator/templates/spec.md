# Spec: <short feature name>

- **Status:** draft | approved
- **Derived from:** `<path to intent.md>`
- **Date:** <YYYY-MM-DD>

## Summary
<One paragraph: what will be built and how, at a level a reviewer can hold in
their head.>

## Architecture
<Components, data flow, and where the change lands in the existing system.
Include a diagram if the flow is non-obvious.>

## Interfaces and contracts
<APIs, function signatures, schemas, events, error shapes. Be concrete enough
that Phase 3 can generate code without re-deciding anything.>

## Data model changes
<Migrations, new fields, backfill strategy, rollback plan. "None" is a valid
answer — state it.>

## Institutional standards applied
<Which conventions from AGENTS.md / CLAUDE.md / copilot-instructions.md / ADRs
and neighbouring code shaped these decisions.>

## Trade-offs considered
| Option | Pros | Cons | Decision |
|--------|------|------|----------|
| <...>  |      |      | chosen / rejected because <...> |

## Test strategy
- **Unit:** <...>
- **Property-based / invariants:** <invariants implied by the requirements>
- **Edge cases:** <empty, boundary, concurrent, malformed, unauthorized>
- **Output eval:** <build + test command that must pass>
- **Trajectory eval:** <safety/validation/authorization steps that must appear
  in the implementation path>

## Security and governance
<AuthN/AuthZ, secret handling, PII, audit trail. Mark explicitly whether this
change is critical or regulated and therefore requires the human gate in
Phase 5.>

## Risks and rollback
<What could go wrong in production, how it would be detected in Phase 6, and how
to roll back.>
