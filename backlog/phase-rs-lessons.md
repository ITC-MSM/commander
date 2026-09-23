# Phase (phase-rs) lessons for Argentum

Design ideas worth adopting from [phase-rs/phase](https://github.com/phase-rs/phase), an MTG engine
that reached ~34k *parsed* cards via a nom oracle-text → IR → engine-types pipeline. We are **not**
adopting its runtime text-parsing or its `Effect::Unimplemented`-as-default permissiveness — our
typed, fully-authored, scenario-tested DSL is the more robust core. What's worth taking are a few
structural moves where phase is more elegant than us.

**Companion docs:**

- [engine-sdk-architecture-review.md](engine-sdk-architecture-review.md) §5 — "one spelling per concept"; composition over enumeration, and retiring
  predecessors once a general primitive lands. This doc's Lesson 1 is a concrete instance of that premise.

## The lessons

| # | Lesson | Status |
|---|--------|--------|
| **1** | **Unify the optional/gated-effect cluster behind one resolution frame** | **Detailed below — HIGH** |
| **2** | **Snapshot/golden tests of compiled `CardDefinition` effect trees** | **Detailed below — prerequisite for #1; small** |
| **5** | **Feature-coverage probe — which unimplemented cards the engine can already support** | **Detailed below — HIGH value, tooling-only** |
| 3 | CR-rule citations as a doc-comment convention on SDK types | Detailed below — MED, mechanical |
| 4 | Typed coverage diagnostics (silent no-ops / escape-hatch inventory) | Detailed below — MED, shares the capability registry with #5 |

Lesson 2 should land **first** — it is the safety net that makes Lesson 1 migrations reviewable
(see Rollout, Phase 0).

---

## Lesson 1 — One resolution frame for the optional/gated-effect cluster — [HIGH]

### Problem

We model every cross-cutting "do something conditionally" concern as **its own wrapper `Effect`
type, with its own executor, and its own interaction with target-locking and the pause/continuation
machinery.** In one file —
[`CompositeEffects.kt`](../mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/scripting/effects/CompositeEffects.kt) —
the cluster is already 11+ types:

| Type | What it encodes | Executor |
|------|-----------------|----------|
| `MayEffect` | yes/no decision → inner | `MayEffectExecutor` |
| `IfYouDoEffect` | run action, gate on outcome → inner | `IfYouDoEffectExecutor` |
| `OptionalCostEffect` | may pay a cost-primitive → inner | `OptionalCostEffectExecutor` |
| `MayPayManaEffect` / `MayPayXForEffect` | may pay {mana}/{X} → inner | (composite) |
| `BlightEffect` / `TapCreatureForEffectEffect` | may pay a *specific* cost → inner | (composite) |
| `PayOrSufferEffect` | inner unless you pay | (composite) |
| `AnyPlayerMayPayEffect` | APNAP "any player may pay" → branch | (composite) |
| `ReflexiveTriggerEffect` | may act → reflexive trigger (target after) | (composite) |
| `BeholdEffect` | may behold → inner | (composite) |
| `ConditionalEffect` | game-state `if X` → inner/else | (composite) |

Every one of these expresses the **same shape**: a *gate* (decide / do-an-action / pay-a-cost /
check-a-condition / any-player-pays) → a `then` effect that runs iff the gate succeeds → an optional
`otherwise` → a *decision-maker* → a *success criterion*. They differ only in the gate kind and the
success test. Yet each is a distinct serialized type + executor + continuation-resumer touchpoint.

**Why this is the bug factory, not just sprawl.** Because the modifiers are *wrapper types*, they
compose by **nesting**, and the nesting order is load-bearing and chosen per-card:

```kotlin
// "you may, if you do, destroy target creature" — target locks at trigger time (CR 603.3d),
// the "may" prompt waits until resolution (CR 117.3a). Nothing structural enforces that.
MayEffect(IfYouDoEffect(action, DestroyEffect(target = EffectTarget.creature())))
```

This is exactly the shape behind our recurring continuation-flow bugs (see working memory):
*MayEffect + target order*, *Triggers lost on mid-resolution pause*, *Modal cast loses per-mode
targets*, and the **Ward—Sacrifice** fix that needed a brand-new `CounterUnlessSacrificeContinuation`
because the unless-cost machinery wasn't unified. Each new wrapper multiplies the
wrapper × target-locking × pause/resume interaction surface.

**Phase's contrast.** phase folds these into one clause envelope (`ParsedEffectClause`) carrying
`optional`, `condition`, `unless_pay`, `duration` as *orthogonal fields*, and a **single resolver
unwinds them in a fixed canonical order**. You cannot transpose may-vs-target per card because there
is no nesting to transpose.

**We've already proven this works locally.** `CounterEffect` + the `CounterCondition` sealed interface
([`StackEffects.kt:84`](../mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/scripting/effects/StackEffects.kt))
is precisely this move for unless-cost — one pipeline, `UnlessPaysMana` / `UnlessPaysDynamic` as
variants, **no** per-cost continuation. We never got a "counter-unless-pay vs counter-unless-sacrifice
ordering bug" there. Lesson 1 generalizes that instinct to the gated-effect cluster.

This is **composition over enumeration** ([engine-sdk-architecture-review.md](engine-sdk-architecture-review.md) §5), not "adding params
to an atomic effect" (which [feedback] forbids): we are replacing an *enumeration of 11 wrappers* with
*one composable frame + a gate algebra*.

### Proposal

One `GatedEffect` + a `Gate` sealed hierarchy, resolved by **one** executor and **one** continuation
resumer that owns the canonical unwind order.

```kotlin
@SerialName("Gated")
@Serializable
data class GatedEffect(
    val gate: Gate,                              // what must succeed first
    val then: Effect,                            // runs iff the gate succeeds
    val otherwise: Effect? = null,               // runs iff the gate fails ("if you don't")
    val decisionMaker: EffectTarget = EffectTarget.Controller,
    val descriptionOverride: String? = null,
) : Effect

@Serializable
sealed interface Gate {
    /** Pure yes/no.                       ← MayEffect */
    data class MayDecide(val prompt: String? = null, val hint: String? = null) : Gate
    /** Run an action; success = it did work, per criterion.  ← IfYouDoEffect */
    data class DoAction(val action: Effect, val criterion: SuccessCriterion = SuccessCriterion.Auto) : Gate
    /** Optionally pay a cost; success = paid. ← OptionalCost/MayPayMana/MayPayX/Blight/TapCreature */
    data class MayPay(val cost: PayCost) : Gate
    /** Game-state predicate.              ← ConditionalEffect's leading "if X" */
    data class WhenCondition(val condition: Condition) : Gate
    /** APNAP "any player may pay".        ← AnyPlayerMayPayEffect (see Risks) */
    data class AnyPlayerMayPay(val cost: PayCost) : Gate
}
```

**The canonical resolution order, owned in one place:**

1. **Lock targets** for `then` / `otherwise` at the rules-correct time (trigger-time, CR 603.3d) —
   *before* the gate prompt, independent of gate kind.
2. **Evaluate** a leading `WhenCondition` (CR 608.2c) if present.
3. **Resolve the gate**: prompt the `MayDecide` / attempt the `MayPay` payment / run the `DoAction` —
   at resolution time (CR 117.3a), via the **decisionMaker**.
4. **On success → `then`; on failure → `otherwise`.**
5. **Apply duration** if the frame carries one (see Out of scope).

Because steps 1–5 live in the resumer, target-locking-vs-decision timing is correct **by
construction** for every gate — the bug class disappears rather than being re-fixed per wrapper.

**Facades stay stable.** `Effects.IfYouDo` ([`Effects.kt:1675`](../mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/dsl/Effects.kt)),
the may/optional-cost facades, etc. keep their signatures and simply construct a `GatedEffect`
underneath. **No card source changes** — only the lowered representation changes, which is exactly
what the Lesson 2 snapshot net is for.

### Cross-layer trace (per `add-feature`)

- **SDK** — add `GatedEffect` + `Gate` (both `@Serializable`, `@SerialName`d). Reuse the existing
  `SuccessCriterion` and `PayCost` types as-is. Repoint facades.
- **Engine** — one `GatedEffectExecutor` + one `GatedContinuationResumer` replace
  `MayEffectExecutor`, `IfYouDoEffectExecutor`, `OptionalCostEffectExecutor`, and the may/pay tails of
  the composite executors. The resumer owns pause/resume across cost payment (the part that currently
  leaks into `ManaPaymentContinuationResumer` / `SacrificeAndPayContinuationResumer`).
- **Projection** — none; this is resolution-time only. (Sanity-check that gate-evaluated conditions
  route through `matchesWithProjection` where they read battlefield state.)
- **Server DTO** — the decision prompts these wrappers emit (yes/no, cost payment, choose) should
  collapse toward one `DecisionRequest` shape. Verify in [`docs/data-contracts.md`](../docs/data-contracts.md).
- **Client** — should need no change (it already renders yes/no + cost-payment prompts); confirm one
  unified prompt path. Trace per [feedback] UX review.

### Rollout — phased, each phase shippable and green

- **Phase 0 — Net + catalog.** Land **Lesson 2** (snapshot of every registered `CardDefinition`'s
  effect tree) first. Enumerate the full cluster and ensure each member has a behavioral scenario test
  pinning current behavior. The snapshot diff + scenario suite are the migration's guardrails.
- **Phase 1 — Introduce the frame.** Add `GatedEffect` / `Gate` + the unified executor and resumer.
  Migrate **nothing** yet; new cards may opt in. Verifies the canonical-order resolver against fresh
  scenarios (esp. the may-vs-target timing cases).
- **Phase 2 — Migrate one wrapper at a time.** Repoint each facade to build `GatedEffect`; delete that
  wrapper's bespoke executor. Each migration gated by: snapshot diff reviewed + its scenario suite
  green. Order by shared-surface: `MayEffect` → `IfYouDoEffect` → `OptionalCostEffect` →
  `MayPayMana`/`MayPayX`/`Blight`/`TapCreature` as `Gate.MayPay` variants.
- **Phase 3 — Retire.** Once no callers remain, delete the redundant types + executors, collapse the
  decision DTOs, and update [`docs/card-sdk-language-reference.md`](../docs/card-sdk-language-reference.md)
  in the same change ([mtg-sdk load-bearing rule]).

### Pilot

Smallest coherent slice: `MayEffect` + `IfYouDoEffect` + `OptionalCostEffect` (each has a dedicated
executor to delete and they share the most behavior). Prove the canonical-order resolver kills the
may-vs-target timing bug on a real card before widening.

### Out of scope / explicitly NOT folded

- **`BeholdEffect`** stays its own type — behold's reveal/identity semantics are identity, not gate
  parameters ([feedback] *Don't generalize Behold*). It may *contain* a `GatedEffect` as its payoff.
- **`ReflexiveTriggerEffect`** — its target is chosen *after* the action (CR reflexive trigger), a
  genuinely distinct timing axis. Defer; possibly model later as `Gate.DoAction` + a post-action
  target requirement, but don't force it.
- **`Duration`** stays a clean field on the effects that need it
  ([`Duration.kt:26`](../mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/scripting/Duration.kt)); fold into
  the frame only if a concrete card needs gate+duration together.
- **unless-cost (`CounterCondition`)** is already unified; leave it unless `Gate` subsumes it cleanly.

### Risks

- **Serialization / replays.** Card defs are recompiled and re-serialized each load
  (`CardSerializationRoundTripTest`), so no stored-card migration — but persisted **replays/transcripts**
  may hold old `@SerialName`s. Audit before deleting names in Phase 3; keep deprecated aliases if needed.
- **`AnyPlayerMayPay`** carries APNAP ordering that the single-decisionMaker frame doesn't model.
  Keep it separate initially; fold only if the gate algebra cleanly expresses multi-player order.
- **Surface size.** This touches ~11 types and several executors. Lesson 2's snapshot net is a hard
  prerequisite, not optional.

### Payoff

`N` wrapper types + `N` executors + `N` continuation touchpoints → `1 + 1 + 1`. The may/target
ordering bug class is eliminated by construction rather than re-fixed per wrapper. A future
"you may [new cost], if you do, [effect]" mechanic becomes a new `Gate` variant — not a new
effect + executor + resumer triple.

---

## Lesson 2 — Golden snapshots of every compiled `CardDefinition` — [MED, do first]

### Problem

Our test net is **behavioral and sparse**: Kotest scenario tests assert game outcomes, and they exist
for maybe a few hundred cards. The serialization net
([`CardSerializationRoundTripTest.kt`](../mtg-sdk/src/test/kotlin/com/wingedsheep/sdk/serialization/CardSerializationRoundTripTest.kt))
only checks that *hand-picked* cards survive a JSON round-trip — it asserts nothing about the
thousands of cards that have neither a scenario nor a round-trip case.

So when we do an **SDK refactor** — the FacadeBoundary work, the `Condition` unification (which
collapsed the `You*`/`Controller*` triples), `EffectPatterns` → pattern objects, or **Lesson 1's
gated-effect migration** — we have *no cheap signal of the blast radius*. A change that subtly alters
the compiled effect tree of a card with no scenario test ships silently. The only way we currently
find out is a self-play game (or a player) hitting wrong behavior later.

phase commits per-card `*_lowered.snap` golden files for exactly this reason: any change to their
lowering shows up as a reviewable diff across the whole corpus.

### Proposal

A single Kotest snapshot test that walks **every registered card**, serializes its compiled effect
tree to canonical JSON, and asserts against a committed golden file. On any SDK change, the test fails
with a precise per-card diff of what moved — turning "silent blast radius" into "reviewable diff."

The plumbing already exists; we are wiring it together, not building it:

- **Enumerate every card** — `MtgSetCatalog.all` ([`MtgSetCatalog.kt`](../mtg-sets/src/main/kotlin/com/wingedsheep/mtg/sets/MtgSetCatalog.kt))
  exposes every `MtgSet`; each `MtgSet.cards` ([`MtgSet.kt:29`](../mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/model/MtgSet.kt))
  is its `List<CardDefinition>`.
- **Canonical serialization** — `CardExporter.exportToJson`
  ([`CardExporter.kt`](../mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/serialization/CardExporter.kt))
  already produces stable, pretty-printed, `CompactJsonTransformer`-normalized JSON (the same shape it
  uses for the Kotlin→JSON migration). Reuse it verbatim so the snapshot matches a format we already
  trust round-trips.

```kotlin
class CardDefinitionSnapshotTest : DescribeSpec({
    describe("compiled card-definition snapshots") {
        MtgSetCatalog.all.forEach { set ->
            it("${set.code}: card trees match golden") {
                val actual = set.cards
                    .sortedBy { it.name }
                    .joinToString("\n") { "// ${it.name}\n${CardExporter.exportToJson(it)}" }
                // golden lives at src/test/resources/snapshots/cards/<code>.json
                actual shouldMatchGolden "snapshots/cards/${set.code}.json"
            }
        }
    }
})
```

One golden file **per set** (not per card) — keeps the tree small, diffs reviewable, and the test
count bounded. `shouldMatchGolden` is a thin helper: compare to the resource; on mismatch, write the
actual to `build/` and fail with the path so the author can eyeball-then-`UPDATE_SNAPSHOTS=1` to
re-bless. (Kotest has no built-in approval matcher; ~20 lines, or pull in a small approval lib —
decide in Phase 0.)

### What it catches (worked example)

The `Condition` unification touched every card using a `You*`/`Controller*` condition. Most have no
scenario test. With snapshots, that refactor produces:

```diff
  // Tendershoot Dryad
-       "condition": { "type": "YouHaveCitysBlessing" }
+       "condition": { "type": "HasCitysBlessing", "player": "CONTROLLER" }
  // Crested Sunmare        ← no scenario test exists for this card
-       "condition": { "type": "YouGainedLifeThisTurn" }
+       "condition": { "type": "GainedLifeThisTurn", "player": "CONTROLLER" }
```

Author scans the diff: pure equivalent renamings → re-bless. But if one card flipped to
`"player": "ACTIVE"`, the diff surfaces it *before* it ships. This is the guardrail Lesson 1's Phase 2
migrations are gated on.

### Cross-layer trace

Test-only. No SDK, engine, server, or client change. Touches `mtg-sets` test scope (it depends on
`mtg-sdk`, so it can see both the catalog and `CardExporter`). Confirm the module placement —
`mtg-sets/src/test` is the natural home since that's where the full catalog is on the classpath.

### Rollout

- **Phase 0 — Helper + bless.** Add `shouldMatchGolden` (or adopt an approval matcher) and an
  `UPDATE_SNAPSHOTS` env switch. Generate the initial goldens and commit them. This is the whole
  feature for existing cards.
- **Phase 1 — Wire into the normal suite.** Runs under `just test`. Document the re-bless workflow in
  the test's KDoc and a line in [`docs/e2e-test-patterns.md`](../docs/e2e-test-patterns.md) /
  testing-strategy.
- **Ongoing** — `add-card` produces a new golden as a side effect of its scenario test run; the diff
  is part of the card's PR. No separate authoring step.

### Risks / caveats

- **Churn on intentional changes.** Every deliberate SDK rename re-blesses many goldens — that's the
  *point*, but PRs get large diffs. Mitigate with per-set files (localized blast radius) and a clear
  "re-bless" command so reviewers know a green re-bless is expected.
- **Non-determinism.** `CardExporter` must emit stable ordering (it does — pretty-print + compact
  transform). Sort cards by name; avoid serializing any field with run-dependent values. Verify no
  `hashCode`/set-iteration ordering leaks into the JSON.
- **Not a correctness oracle.** A snapshot proves "the compiled tree didn't change unexpectedly," not
  "the card is correct." It complements scenario tests; it does not replace them.

### Payoff

Cheapest item in this doc with the highest leverage: the serialization path already exists, so it's a
few hours of wiring for a permanent, corpus-wide regression net under every future SDK refactor —
and the hard prerequisite that makes Lesson 1's migration safe to land at scale.

---

## Lesson 5 — Feature-coverage probe: which unimplemented cards the engine can already support — [HIGH value, tooling-only]

### Problem

Our backlog is framed by **card count**: [`scripts/card-status`](../scripts/card-status) diffs Kotlin
source against Scryfall and tells us *which* cards are missing per set. It tells us nothing about
**why** a card is missing, or what it would take to add it. Two very different cards sit in the same
"missing" bucket:

- *"Destroy target creature with flying."* — every primitive exists; this is **pure authoring time**.
- *"Whenever you cast a spell, amass Zombies equal to its mana value."* — needs an **engine feature**
  (`amass`) before it can be authored at all.

Without separating those, we can't answer the questions that actually drive throughput:

- Of this set's 80 missing cards, how many can a contributor implement *today* with no engine change?
- Which **missing feature**, if added, unlocks the most cards — across this set and the whole backlog?

phase gets this for free: its oracle-text parser emits `Effect::Unimplemented` for any clause its
grammar can't model, so "fully parses" ⇒ "the engine already covers it," and the gaps are an
automatic feature to-do list. We deliberately **don't** parse oracle text in the engine, and
shouldn't — so we need the same triage signal without that mechanism.

### The architectural twist (read before designing)

The probe is an **offline, non-authoritative triage tool — never a card loader.** It *predicts*
coverability from oracle text; it does not *prove* it. Ground truth stays exactly what it is today: a
human authored the card via the DSL and its scenario test passes. This keeps the "engine is fully
authored, no runtime text parsing" principle intact (it's a `scripts/` analyzer, not engine code),
while recovering phase's prioritization benefit.

Because it's predictive, it will be wrong at the margins — and that's fine, *if* we calibrate it
against ground truth (below). It's a backlog-ranking aid, not a correctness oracle.

### Proposal

Extend `scripts/card-status` (or a sibling `scripts/coverage-probe`, same Python + Scryfall cache) with
three parts:

**1. Capability registry — what the engine supports, generated from source.**
A machine-readable inventory of engine/SDK capabilities, scanned from Kotlin so it can't rot:

- **Keywords** — the `Keyword` enum ([`Keyword.kt`](../mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/core/Keyword.kt))
  and `KeywordAbility` variants ([`KeywordAbility.kt`](../mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/scripting/KeywordAbility.kt)).
- **Effects** — the `@SerialName` tags on `Effect` subtypes / the `Effects.*` facade
  ([`Effects.kt`](../mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/dsl/Effects.kt)).
- **Triggers / conditions / costs / dynamic amounts** — the `Triggers.*`, `Conditions.*`, `Costs.*`
  facades.

This same registry is what Lesson 4 needs — build it once, share it.

**2. Oracle-text → required-capability extractor.**
Maps a card's Scryfall payload (oracle text + the per-card `keywords` array + type line) to a set of
required capability tags. Two precision tiers:

- **Keywords (near-exact).** Scryfall already lists each card's keywords authoritatively. A card's
  keyword set ∖ the engine's `Keyword`/`KeywordAbility` registry = its missing keyword features,
  with essentially no guesswork. This alone is most of the value.
- **Clause templates (heuristic).** Pattern-match common oracle templates — "destroy target {filter}",
  "draw N cards", "deals N damage to any target", "+X/+Y until end of turn", "search your library for",
  "create N {token}" — onto effect tags. Approximate by design; it grows one rule at a time as new
  mechanics appear.

**3. Gap report.**
Per card: `required ∖ supported = missing`; the card is **coverable-now** iff `missing` is empty.
Aggregate into a **feature leaderboard** — missing capabilities ranked by how many backlog cards each
would unlock.

### The calibration loop (this is what makes it trustworthy)

We have thousands of **already-implemented** cards — a free labeled validation set. Run the extractor
over them: **every implemented card must classify as coverable-now.** Any implemented card the
extractor flags as "needs feature X" is a hole in the extractor or registry (a template it doesn't
recognize, a capability it can't see) — fix it there. This pins recall against ground truth.

The other direction closes over time: when a contributor picks a "coverable-now" card and discovers it
*did* need an engine change, that's a labeled false-positive — feed it back as a new extractor rule.
Precision climbs with use. (phase's coverage is ground-truth because its parser produces runnable IR;
ours is *calibrated prediction* — the implemented corpus is how we keep it honest.)

### Output / UX

```
$ scripts/card-status --coverage --set BLB
  Coverable now (no engine change):  47
  Blocked on engine features:        18
    Offspring        ×7   (keyword: not in Keyword enum)
    Forage           ×4   (cost: not in Costs facade)
    Expend N         ×3   (trigger: not modeled)
    ... "deal damage equal to defending creatures' total power" ×1 (clause template unrecognized)

$ scripts/card-status --coverage --leaderboard      # across the whole backlog
  Top features by cards unlocked: Bestow (61), Monstrosity (38), Adventure-on-land (12), ...
```

`--list --coverage` annotates each missing card with `[coverable]` or `[needs: forage]`, so
spoiler-season triage is immediate: start authoring the coverable pile; route the blocked pile through
`add-feature`.

### Cross-layer trace

**Tooling only.** A Python script + a source scan; reuses `card-status`'s Scryfall cache and
Kotlin-source name-scan. No engine, SDK, server, or client change. The capability registry is the one
shared artifact (with Lesson 4). Scryfall fetch goes through the script's existing `urllib` path
(WebFetch/curl are sandbox-blocked for Scryfall; the script already uses urllib).

### Rollout

- **Phase 0 — Keyword coverage (near-exact, immediately useful).** Scryfall `keywords` per card vs the
  `Keyword`/`KeywordAbility` registry. Ships the leaderboard for keyword features with almost no
  heuristic risk. This is the bulk of the value for the least effort.
- **Phase 1 — Clause templates + calibration.** Add the high-frequency oracle templates; run the
  calibration loop over the implemented corpus until implemented cards classify ~100% coverable.
  Report measured precision/recall in the script so its trust level is visible.
- **Phase 2 — Integrate.** `--coverage` / `--leaderboard` flags; `--list` annotations. Wire into the
  `add-feature` skill so a new feature's PR can state "unlocks N backlog cards," and into spoiler-season
  set triage.
- **Phase 3 — Feedback.** Capture authoring false-positives (predicted coverable, actually needed
  engine work) as new extractor rules; the probe sharpens with each set.

### Risks / caveats

- **Heuristic imprecision** on the clause-template tier — over- and under-claiming both happen.
  Mitigated, not eliminated, by calibration; always present it as advisory ranking, never as a gate.
- **Template drift.** Every new set ships new wordings; unrecognized clauses surface as "unknown
  template" (not silently coverable) so they're visible as extractor work — which is itself a useful
  signal about novel mechanics.
- **Not correctness.** "Coverable-now" means "no *new* primitive needed," not "the obvious composition
  is correct." Authoring + scenario test remain the source of truth.

### Payoff

Flips backlog planning from card-count to **feature ROI**: one glance shows which missing features
unlock the most cards, and which cards a contributor can pick up today with zero engine work. Turns
spoiler season into a sorted worklist (coverable pile vs feature-blocked pile) and gives `add-feature`
an impact estimate per feature. Tooling-only, built on infrastructure that already exists.

---

## Lesson 3 — CR-rule citations as a doc-comment convention on SDK types — [MED, mechanical]

### Problem

Our [CLAUDE.md] makes rule-number verification a hard rule — *"613.8 vs 613.7, 704.5 vs 704.6 … look
it up first … download the `.txt` and grep"* — precisely because these are easy to misremember. But
the verified number then lands in a **commit message or an ad-hoc comment**, invisible at the type and
call site. A reviewer reading a layer-7 stat-setting effect, or the trample damage-assignment path
(our fix turned on CR 702.19c), can't tell *which* rule it implements or whether it's the right one.
The verification happened once, in a place the next reader never sees.

phase bakes the citation into the type. `ParsedEffectClause`'s fields are annotated inline —
`// CR 601.2d` (distribution), `// CR 115.1d` (multi-target), `// CR 608.2c` (leading condition),
`// CR 117.3a` ("may"), `// CR 118.12` ("unless pays"). The rules anchor is part of the artifact, so
it's reviewable and greppable, not tribal knowledge.

### Proposal

A KDoc convention: every **rules-load-bearing** SDK type cites the CR rule(s) it models, in a
consistent greppable form (`CR 702.19c`). Scope it to the types where the rule number is genuinely
load-bearing and easy to get wrong — not every atom:

- **Layer system** — the 613.x continuous-effect / dependency types (already documented in
  [continuous-effect-dependency-system.md](../docs/continuous-effect-dependency-system.md)); cite the
  sub-rule each layer-affecting effect lives in (613.x, 613.4 sublayers).
- **Combat** — first strike / double strike / trample / lethal-damage assignment (509/510/702.19/702.4).
- **Replacement & prevention** (614/615/616), **state-based actions** (704.5x), **keyword abilities**
  (702.x — one per `Keyword` / `KeywordAbility` variant).

Atoms whose semantics are obvious (`DrawCardsEffect`, `GainLifeEffect`) don't need a citation —
over-citing is its own noise. The bar is "could a competent reviewer reasonably disagree about the
rule, or misremember the number?"

### Enforcement (light)

- Add it to the [mtg-sdk load-bearing rules] alongside the existing *"update the DSL reference in the
  same change"* rule, and to the `add-card` / `add-feature` checklists.
- Optional grep-lint test: assert that types in the layer-system / keyword / replacement packages
  carry a `CR ` citation in their KDoc. ~20 lines, runs in `mtg-sdk:test`, catches omissions on new
  types without policing the whole `Effect` hierarchy.
- [`docs/card-sdk-language-reference.md`](../docs/card-sdk-language-reference.md) can then carry a
  CR → type cross-index, generated from the citations.

### Cross-layer trace

Doc-comments + the language-reference index + an optional grep-lint. No runtime, no serialization, no
behavior change.

### Rollout

- **Phase 0 — Convention + highest-risk cluster.** Write the convention; apply it first to the layer
  system (613.x) and combat damage (509/510/702.19) — the densest concentration of misremember-able
  numbers and the place a wrong citation does the most damage.
- **Phase 1 — Backfill on touch.** `add-feature` / `add-card` cite the rule for any new rules-bearing
  type; backfill opportunistically when editing nearby code. No big-bang sweep.
- **Phase 2 — Index + lint.** Add the grep-lint and the CR cross-index to the language reference.

### Risks / caveats

- **Renumbering.** WotC occasionally renumbers; citations can go stale. Low frequency, and a stale
  citation is still better than none — pair with the existing "verify against the downloaded `.txt`"
  rule when touching the code.
- **Over-citation noise.** Keep the scope tight (rules-load-bearing types only); a citation on every
  trivial effect dilutes the signal.

### Payoff

Turns "verify the rule number" from per-author memory discipline into a reviewable, greppable artifact
on the type itself — self-documenting layer / combat / replacement / SBA code, and a language reference
that indexes by Comprehensive Rule. Cheap and incremental; no flag day.

---

## Lesson 4 — Typed coverage diagnostics: catch silent no-ops and inventory escape hatches — [MED, shares registry with #5]

### Problem

Two real, silent hazards exist among **already-implemented** cards, and nothing surfaces either today:

1. **Silent unhandled-effect no-op.** The effect dispatcher fails open:
   [`EffectExecutorRegistry.kt:110`](../rules-engine/src/main/kotlin/com/wingedsheep/engine/handlers/effects/EffectExecutorRegistry.kt)
   — `?: return EffectResult.success(state) // Unhandled effect type`. If a card's compiled tree
   references an `Effect` type that has **no registered executor**, the effect *silently does nothing*
   and the resolution reports success. There is no build error, no log, no test — it surfaces only as
   a card behaving wrong in a real game. (A sibling fail-open lives at
   [`OptionalCostEffectExecutor.kt:108`](../rules-engine/src/main/kotlin/com/wingedsheep/engine/handlers/effects/composite/OptionalCostEffectExecutor.kt)
   — "unknown effect shapes are assumed payable.")

2. **Invisible bespoke escape hatches.** Genuinely per-card logic is routed through
   [`CardSpecificContinuationResumer`](../rules-engine/src/main/kotlin/com/wingedsheep/engine/handlers/continuations/CardSpecificContinuationResumer.kt)
   (SecretBid, OpenLifeBid, ContestedRetarget — Psychic Battle, etc.). That's legitimate, but there's
   no **inventory** of which cards depend on non-generalized paths, so we can't see generalization
   candidates or gauge how much bespoke surface we carry.

`scripts/card-status` tells us a card *exists*; it cannot tell us the card is modeled with real,
executable primitives. This is the inverse of Lesson 5: #5 finds **unimplemented** cards we *could*
add; #4 finds **implemented** cards that are *fragile or hollow*.

We are **not** adopting phase's `Effect::Unimplemented`-as-default permissiveness. We want only the
observability half: structured findings about our own modeled cards.

### Proposal

A health pass over every registered `CardDefinition` (test-time, optionally also a dev-mode log) that
emits structured findings:

1. **Dangling-effect check (hard, highest value).** Build the set of `Effect` `@SerialName`s the SDK
   defines, and the set of effect types `EffectExecutorRegistry` has executors for. For every
   registered card, walk its effect tree; any referenced effect type *not* in the executor set is a
   silent-no-op risk. Make this a **CI test that fails the build** — converting the
   `// Unhandled effect type` trap into a compile-time gate. (The SDK-tag inventory is exactly Lesson
   5's capability registry — build it once, share it.)
2. **Escape-hatch inventory (report).** List the cards that route through `CardSpecificContinuationResumer`
   or other per-card bespoke paths. Output as a tracked report — generalization candidates, and a
   measure of bespoke surface over time.
3. **Fail-open audit (report, optional).** Surface effect shapes that hit the "assumed payable" /
   unknown-shape fallbacks, so the fail-open defaults are deliberate, not accidental.

### Cross-layer trace

Test + tooling. Reads SDK `@SerialName`s, the engine's `EffectExecutorRegistry`, and `MtgSetCatalog`.
No runtime behavior change (the optional dev-mode log aside). The capability registry is shared with
Lesson 5.

### Rollout

- **Phase 0 — Dangling-executor CI gate.** The check that no registered card references an
  executor-less effect. Needs an **allowlist** of intentionally executor-less effects (markers, and
  effects handled only inside a composite/continuation rather than via a standalone executor) so the
  gate is signal, not noise. Highest value for least effort — it's a latent-correctness net, not just
  coverage.
- **Phase 1 — Escape-hatch inventory.** The `CardSpecificContinuationResumer` (and similar) card list,
  emitted as a report.
- **Phase 2 — Integrate.** Share the capability registry with Lesson 5; optionally add a dev-mode
  diagnostic log when a card loads with a flagged shape.

### Risks / caveats

- **Legitimately executor-less effects.** Some effects are handled inside composites/continuations or
  are pure markers; without the allowlist, Phase 0 is noisy. The allowlist is the real design work and
  must be curated, not auto-derived.
- **Fail-open is sometimes correct.** "Assume payable on unknown shape" is the right default in places
  ([`OptionalCostEffectExecutor.kt:108`]); the audit should *report* these, not blanket-fix them.
- **Not correctness.** Confirms each effect has *an* executor, not that the composition is right —
  scenario tests remain the source of truth.

### Payoff

Converts the silent `// Unhandled effect type` success into a CI failure — a latent-correctness net
across the whole card corpus — and gives a tracked inventory of bespoke escape hatches as
generalization candidates. Together with Lesson 5, the two probes cover both halves of coverage:
what we could add, and what we've added that isn't solid.
