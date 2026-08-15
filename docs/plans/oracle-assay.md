# Plan: Argentum Assay — a first-party Oracle-text parser

Design: [`docs/oracle-assay.md`](../oracle-assay.md). This is the build order.

## Goal

Replace `Scryfall → mtgish (Go) → mtgish.lines.json → emitter → Kotlin source` with
`Scryfall → Assay → mtg-sdk model → Kotlin source`, using a bidirectional grammar whose
correctness is proved by a corpus-wide round trip rather than by review.

Decisions (locked):

- **Bidirectional or it doesn't ship.** A rule registers `build` (text→model) and `match`
  (model→text) together. No parse-only rules, ever — the gate is the whole point.
- **No intermediate representation.** The grammar targets `mtg-sdk` types directly.
- **Declining is success.** Unparseable text is counted and ranked, never approximated.
- **Per-set cutover, no flag day.** A set moves to Assay only when Assay's whole-render rate on
  that set beats `:mtgish-tooling`'s. `:mtgish-tooling` stays authoritative until Phase 6.
- **New module `:oracle-assay`, depending on `:mtg-sdk` only.** Not on `:rules-engine`, not on
  `:mtgish-tooling`.

## Core insight: most of the substrate already exists

- **The target vocabulary is already factored.** `mtg-sdk/dsl/PipelineBuilder.kt` is
  `gather` → `chooseExactly`/`chooseUpTo`/`chooseAnyNumber`/`selectAll`/`chooseRandom` →
  `move`/`destroy`/`sacrifice`/`exile`/`toHand`/`toGraveyard`, over typed slots. The parser's output
  shape is a solved problem; we are not designing an IR.
- **Scryfall ingestion exists.** `mtgish-tooling/.../coverage/Scryfall.kt` already fetches and caches
  set data. Assay needs the bulk `oracle_text`, which is the same source.
- **The semantic oracle exists.** 8,728 canonical `cardDef`s and 2,528 scenario tests are ground
  truth for gates 2 and 3, and they are already in the repo.
- **The compile gate exists.** `mtg-sets:verifyGeneratedCards` already compiles generated drafts and
  serializes them; it needs re-pointing at a model comparison, not rebuilding.

**What genuinely does not exist:** the bidirectional `Phrase` kernel, invertible normalization, and
the grammar itself. The grammar is the long pole and always will be.

---

## Phase 0 — Decide to start (no code)

Read the design doc, confirm the premise, and pick a first-milestone fineness target to be judged
against. Kill criteria for the whole project, agreed up front:

- Phase 1 ships and the touchstone cannot get vanilla + keyword-only cards past ~95% ‰. If the
  round trip can't hold on the easy quarter of the corpus, it won't hold anywhere.
- Phase 2 stalls below the incumbent's whole-render rate on any calibrated set (POR is the usual
  canary).

Neither costs anything to check, and both are cheap exits.

---

## Phase 1 — Kernel, normalization, gate harness

The riskiest phase, because it decides whether the round trip is achievable at all. Deliberately
paired with a trivial grammar so the *machinery* is what's under test.

New module `:oracle-assay` (`settings.gradle.kts`, `build.gradle.kts` modelled on
`mtgish-tooling/build.gradle.kts` but with an `implementation(project(":mtg-sdk"))`).

1. **`syntax/Phrase.kt`** — the kernel:
   - `phrase<T>(template) { slot(...); build { }; match { } }`
   - template parsing (`"destroy target {obj}"` → literal/slot sequence)
   - `parse(text): List<Parse<T>>` — *all* parses; `print(value: T): String?`
   - `canonical = false` for alternates that parse but never print
   - memoization keyed on (rule, offset); a per-span parse cap that degrades to a decline
2. **`normalize/`** — Scryfall JSON → canonical ability lines, **each pass with its inverse**:
   name→`~`, reminder-text strip/regenerate, line split, face split, symbol lexing.
3. **`gate/Touchstone.kt`** — run `print(parse(normalize(t))) == normalize(t)` over the bulk;
   emit the fineness report with declines ranked by cards blocked.
4. **`grammar/`** — vanilla cards and keyword-only abilities only. Nothing else.
5. **`cli/`** — `assay parse <card>`, `assay gate --touchstone`, `assay report`.
   `just assay-gate` / `just assay-report` recipes.

**Acceptance:** fineness reported for the whole corpus; ambiguity count is 0; every normalization
pass round-trips; `assay explain <card>` prints the token a decline died on.

**Risk to watch:** if printing turns out to be underdetermined in ways `canonical = false` can't
resolve cleanly, that is the signal to reconsider the whole approach — at the cost of one phase.

---

## Phase 2 — The pipeline family

The largest and most mechanical share of the corpus, and the part where the SDK vocabulary already
lines up. This is where the fineness curve should climb steeply.

Grammar, roughly in dependency order:

1. `Cardinals.kt` — "a", "two", "X", "that many", "equal to the number of…" → `DynamicAmount`
2. `Filters.kt` — type/subtype/colour/power/toughness/controller predicates → `GameObjectFilter`
3. `Zones.kt` — "your library", "the top three cards of", "your graveyard" → `CardSource`
4. `Targets.kt` — "target creature", "any target", "up to two target…" → `TargetRequirement`
5. `Steps.kt` — destroy / sacrifice / exile / draw / mill / discard / return / tap / untap, all as
   pipeline steps over one selection

**Acceptance:** POR, LEA and a modern set (DFT or FDN) each report fineness; the per-set whole-render
rate is directly comparable to `:mtgish-tooling`'s `gN` figure in the coverage dashboard.

---

## Phase 3 — Differential gate against the 8,728

Turn the implemented corpus into the semantic oracle. This is what catches the reversible-but-wrong
class that the touchstone structurally cannot.

1. **`gate/Differential.kt`** — for each implemented card, parse its Scryfall oracle text and
   structurally diff the model against the hand-written `CardDefinition`'s script.
2. **An explicit fold list** — known-equivalent representations (a `Patterns.*` composition versus
   its expansion; commutative ordering). Reviewed, never grown silently, mirroring the existing
   `fidelity --gate` allowlist discipline.
3. **Triage every divergence.** Expect bugs on both sides; a divergence that turns out to be a bug
   in a hand-written card is a genuine win and should get its own fix + scenario test.

**Acceptance:** divergences enumerated and each one classified as parser bug / card bug / fold. The
count matters less than the fact that none are unexplained.

---

## Phase 4 — Renderer

Model → `cardDef` source, as a pretty-printer rather than a string-assembler.

1. **`render/CardDefPrinter.kt`** — typed model → Kotlin, deriving imports by scanning emitted
   symbols (the approach `emitter/Shells.kt` already uses and which works well).
2. **`render/Folds.kt`** — recognise model shapes and emit the idiomatic `Patterns.*` /
   `Effects.*` spelling. **A fold is admissible only if the compiled result expands back to an
   identical model**, so folds become checked rather than trusted.
3. **Re-point `mtg-sets:verifyGeneratedCards`** at a model comparison:
   `deserialize(compile(render(m))) == m`.

**Acceptance:** the fourth gate is green for every card Assay renders whole.

---

## Phase 5 — Per-set cutover

No flag day. For each set, when Assay's whole-render rate exceeds `:mtgish-tooling`'s, switch that
set's generation over and record the pair of numbers in the PR. Always reversible: both generators
remain runnable throughout.

Start with a calibrated set (POR) where the incumbent's fidelity gate is already trusted, so the
first cutover is measured against a known-good baseline rather than a guess.

---

## Phase 6 — Retire the bridge

Only once no set generates from mtgish any more:

1. Drop the mtgish corpus download and `mtgish-tooling/data/mtgish.lines.json`.
2. Delete the capability dictionary (`coverage/bridge/`) and `emitter/TargetRecovery.kt`.
3. Re-point the coverage dashboard at Assay's decline report — same TUI, better signal, because
   declines name Argentum capabilities instead of mtgish tags.

**The dashboard survives; only its data source changes.** Backlog triage ("which feature unlocks the
most cards?") is the module's real value and is unaffected.

---

## Follow-on, not in scope here

- **User-authored cards.** A JVM parser over SDK types lets Argentum accept pasted Scryfall-shaped
  JSON and return a playable definition. Product feature; needs its own design.
- **The SDK vocabulary findings.** The reach split, half-migrated cardinality, boolean-knob
  accretion, and the `…ThisWay` curve are written up in the design doc's *What this says about
  `mtg-sdk`* section. The proposed one-line policy — *a new mechanic may not mint a `CardSource`
  variant for a value an earlier step could have bound* — belongs in
  [`sdk-design-principles.md`](../sdk-design-principles.md) and should be adopted (or rejected) on
  its own merits, independently of whether Assay gets built.

## Sizing, honestly

The grammar is open-ended: `:mtgish-tooling` is ~21.7k lines and does not cover everything, and
Assay's rules cost roughly 2× a parse-only rule. Phases 1 and 2 are the ones worth committing to up
front; everything after is gated on Phase 2's fineness curve being convincing.

Phase 1 is the real decision point. It is self-contained, produces a corpus-wide number on its own,
and if the round trip doesn't hold there, the cheapest possible exit has already been taken.
