# CommanderOnline card implementation programme

## Scope

The release scope is the Scryfall snapshot and supported-set catalogue at the pinned
repository commit. New printings or sets enter a later, explicitly approved release
scope; they do not silently change the denominator.

Every canonical front-face card must be in exactly one primary status:

- `IMPLEMENTED_UNVERIFIED`
- `IMPLEMENTED_VERIFIED`
- `AUTO_CANDIDATE`
- `SCAFFOLD_REQUIRED`
- `BLOCKED_FEATURE`
- `UNMATCHED_TRIAGE`
- `NOT_PLANNED`

`NOT_PLANNED` is reserved for permanent, documented exclusions such as ante,
subgames, or dexterity cards. Difficulty is never an exclusion reason.

## Definition of verified

A card is verified only when all of the following are true:

1. Its current Oracle text, mechanically significant rulings, and canonical earliest
   printing are checked against Scryfall.
2. The canonical `card(...)` lives in that earliest real expansion, with later
   printings recorded as `Printing(...)` rows.
3. It has one dedicated `<CardName>ScenarioTest.kt`, unless it is only a printing.
4. The test proves normal resolution and all applicable card-specific choices,
   targets, replacements, timing, and last-known-information behaviour.
5. Canonical-placement, build, lint, snapshot, and affected rules-engine gates pass.
6. The generated readiness manifest contains the test and CI evidence for the
   repository commit.

Generator output is a draft, never verification evidence by itself.

## Work loop

1. **Plan** - pin Scryfall/commit inputs and generate a canonical-card ledger.
2. **Assess** - classify each unresolved card and rank missing capabilities by the
   number of cards they unlock.
3. **Fix** - merge rules-engine capabilities before dependent cards; implement cards
   through the repository's `add-card` workflow.
4. **Validate** - run focused scenarios, full affected-module tests, canonical/reprint
   checks, independent review, and readiness-artifact consistency checks.
5. **Re-plan** - regenerate the ledger and review every status or denominator change.

Autonomous set work uses `backlog-loop`: one planner and exactly one implementation
unit in flight, followed by a fresh reviewer and corrector. Work is serial because
canonical cards, snapshots, and shared SDK files otherwise conflict.

## Non-negotiable rules gates

No bulk card work proceeds while any P0 engine gate is failing:

- priority after resolution goes to the active player;
- all-pass, state-based-action, and trigger stabilisation is correct;
- APNAP includes player-selected same-controller trigger order and the additional
  trigger-placement pass required by CR 603.3b;
- commander tax, damage, zone choices, and multiplayer elimination are correct;
- targets, copies, replacements, and last-known information have regression tests.

## Dashboard semantics

The existing Set Completion view remains *catalog coverage*. It must never imply
rules correctness. A separate readiness view reports the primary status above plus
independent evidence flags: catalog, build, snapshot, scenario, rules regression,
Commander regression, and release eligibility. The release gate is a CI-run property,
not a percentage.

Every generated tracker artifact records repository SHA, Scryfall provenance, tool
version, and generation time. CI checks that committed artifacts match source and
opens a reviewable update for denominator changes.
