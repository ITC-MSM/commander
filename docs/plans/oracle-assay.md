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

## The MVP

Decided 2026-08-15, after Phase 1 shipped. The phases below are ordered by *layer*; the MVP is the
vertical slice through them that is worth shipping on its own:

> **Assay reads a whole card and proves the reading against the card we already wrote.**

Not: emit Kotlin, beat `:mtgish-tooling` on a set, retire the bridge. Those are Phases 4–6, and none
of them is where the risk is. Phase 1 proved the machinery is *reversible*; it did not prove it is
*right*, and every rule added before there is a semantic gate is grammar built on unverified
semantics. A renderer on that footing only generates wrong Kotlin faster.

Three parts, in this order:

1. **The differential gate** — done for the class the grammar reads whole; see Phase 3 below.
2. **One vertical grammar band** — the narrowest rule set that makes simple *whole* cards parse:
   cardinals → a small filter/target vocabulary → a handful of pipeline steps → the trigger prefix.
   The decline table ranks this for us: `Whenever` (6,450 cards) and `When` (6,054) are the top two
   families by a wide margin, so triggers are neither deferrable nor a guess.
   *Half done.* `Cardinals` (number words), `Targets` (the requirement/reference pair), `Filters`
   (the noun phrase, with its controller clause) and `Steps` (draw, destroy, exile, tap, untap,
   return to hand — every one-verb spell over a targeted permanent) are in, and the line model
   widened from `List<KeywordAbility>` to [`CardFragment`] so a line can fill either behavioural slot
   a card has. Whole-card coverage 1,744 → 1,906; the differential's compared population 449 → 505,
   and it is now comparing *spells* rather than keyword lists, which is what produced the eight
   findings above.

   **The trigger prefix is the remaining half**, and it is a bigger step than the rules above: a
   trigger is a new `CardScript` slot, so it needs the fragment, the modelled-slot guard and the
   differential's comparison widened together — and it raises three questions the effect rules never
   had to answer. Where a triggered ability declares its *targets*; whether an authored
   `descriptionOverride` is content or presentation (cards set it, a parser never would); and what to
   do with `AbilityId`, which is arbitrary in exactly the way a target slot's name is and will need
   the same normalization.
3. **A third number in the fineness report** — beside "round-trips byte-exact" and "declined", a
   *confirmed* row: whole cards whose model matches the hand-written definition.

**Done when:** several hundred implemented cards parse whole *and* differentially confirm; every
divergence is classified with none unexplained; at least one genuine bug has been found in a
hand-written card; `MISMATCH` and `AMBIGUOUS` are still 0.

Explicitly out: the renderer, per-set cutover, and closing the ~40 missing `Keyword` constants —
that last one is ranked content work with no risk attached, and it inflates Phase 1's number without
teaching anything.

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

## Phase 1 — Kernel, normalization, gate harness ✅ SHIPPED

The riskiest phase, because it decides whether the round trip is achievable at all. Deliberately
paired with a trivial grammar so the *machinery* is what's under test.

**Outcome.** The round trip holds. Over the whole Scryfall Oracle bulk — 34,882 cards, 35,776 faces,
66,793 ability lines — the gate reports **0 ambiguities, 0 print mismatches, and 0 non-invertible
normalizations**. Module docs and the command list: [`oracle-assay/README.md`](../../oracle-assay/README.md).

```
Round-trips byte-exact           12646   189.3‰ (18.9%)   (whole corpus; mostly Phase 2+ text)
Alternate spelling normalized    30
Declined                         54117
Vanilla + keyword-only cards     1439 / 1712   840.5‰ (84.1%)   <- this phase's own target
```

Fineness is parts per thousand, so the target row reads **84.1%** — not 84.05%, and not 840%. The
kill criterion above is written "~95% ‰", which is ambiguous by a factor of ten; the phase lands
below it on either reading.

The shortfall is not the round trip faltering. Every remaining line in that class declines for one
reason: the SDK has no vocabulary for the keyword. `just assay-report --scope` ranks them — Exalted, Infect, Echo,
Soulshift, Bloodthirst, Scavenge, Backup, Megamorph, Unleash, Extort, Evolve, Myriad, Unearth,
Champion, Eternalize, Skulk, Melee, Battle cry, Reinforce, Devoid, Dethrone, Phasing, Cumulative
upkeep, and ~40 more, none of which has a `Keyword` enum constant. Closing that list is content
work with a ranked backlog attached, not a risk to the approach; the machinery it would run on is
proved.

Three findings the phase produced on the way, written up in the module README: `Enchant` and `Equip`
are keyword abilities modelled as an aura restriction and a `CardDefinition` field respectively (the
two largest keyword-only decline families, 1,289 and 621 cards); `PROTECTION_FROM_EACH_OPPONENT` and
`ProtectionScope.EachOpponent` are two spellings of one thing; and printed reminder text is a
function of the ability *and* the card's types, which a `KeywordAbility` alone cannot produce.

**Risk that did not materialize.** Printing turned out to be underdetermined in exactly two places,
and `canonical = false` resolved both cleanly: the semicolon separator ("Flying; banding", ~31 cards)
and line grouping. Both are properties of the printed text that a flat model has no room for, so
normalization owns the second and the first reports as a `VARIANT` — parsed correctly, printed
canonically, model provably unchanged. That verdict is the one addition to the design's vocabulary
this phase made, and it exists so that an alternate spelling is neither counted as a byte-exact
round trip nor as a failure.

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
pass round-trips; `assay explain <card>` prints the token a decline died on. — **All met.**

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

## Phase 3 — Differential gate against the hand-written corpus ✅ HARNESS SHIPPED

Turn the implemented corpus into the semantic oracle. This is what catches the reversible-but-wrong
class that the touchstone structurally cannot. **Brought forward ahead of Phase 2** per the MVP
above: the gate has to exist before grammar breadth, or the breadth is unverified.

**Outcome.** `just assay-differential` runs over all 8,874 committed goldens. Of those, 505 clear
every scoping guard and are compared: **504 confirmed (99.8%), 1 classified divergence**. It found
the predicted class on its first run — multi-quality protection read as one ability where CR 702.16g makes it two,
reversible and wrong — plus two "one concept, two spellings" findings in the SDK. All five opening
divergences are now fixed: the grammar reads a joined quality list as several abilities (and
generalized to subtypes, three-way Oxford lists, and hexproof per CR 702.11f), and the dead
`KeywordAbility.Flanking` object is deleted from `mtg-sdk` — it overrode no `keyword`, so a card
authored with it would have done nothing. Details in [`oracle-assay/README.md`](../../oracle-assay/README.md).

The count is a *checkpoint*, not a property, and it behaved like one immediately: the first band of
spell rules took it from 0 to 8, of which six were the gate's own slot-name normalization colliding
with a field called `target`, one was the positional-versus-named target idiom (now folded, on the
SDK's own statement that they are the same link), and one is a standing finding —
`TargetCreatureOrPlaneswalker` versus the general filtered target, two fully-wired *parallel* engine
paths, deliberately not folded.

**Four guards, three of them found by the gate lying to itself once.** Assay must read every *line*;
the golden's text must be the *same text* Scryfall serves (compared normalized, since goldens carry
reminder text inconsistently); the definition must use only *modelled slots*; and the card's lines
must *fold into one card* — two lines that both parse as the spell effect mean a sequence the grammar
cannot spell, which used to throw and now counts. Every card failing one lands in a named bucket
rather than being confirmed.

1. **`gate/Differential.kt`** — done, over keyword abilities plus `spellEffect` and
   `targetRequirements`. The comparison grows with the grammar, and the three guards above are what
   keep each addition honest. Triggered and static abilities follow as Phase 2 reaches them.
2. **An explicit fold list** — done, as `Folds` in the same file, currently one entry: a bare
   `Keyword` implied by a parameterized `KeywordAbility` of the same keyword, which is a
   `CardDefinition` index entry the SDK populates on purpose rather than a second ability. Reviewed,
   never grown silently.
3. **Triage every divergence.** Ongoing, and the point. The opening five are closed: three were the
   protection-join parser bug, two were the flanking spelling — the first a bug in Assay, the second
   a dead type in the SDK. A divergence that turns out to be a bug in a hand-written card is still
   the outcome worth most, and none has appeared yet; the MVP's "at least one genuine bug found in a
   hand-written card" clause is therefore still open, and is an argument for reaching new card
   classes rather than for polishing this one.

**The oracle is a file read, not a dependency.** `:oracle-assay` still depends on `:mtg-sdk` alone.
The goldens under `mtg-sets/src/test/resources/snapshots/cards/` are data, decoded by `mtg-sdk`'s own
`CardLoader` — which is why this phase cost far less than the plan assumed.

**Acceptance:** divergences enumerated and each one classified as parser bug / card bug / fold. The
count matters less than the fact that none are unexplained. — **Met for the keyword class.**

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
