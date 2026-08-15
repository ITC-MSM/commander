# `:oracle-assay` — Argentum Assay

A first-party Oracle-text parser: Scryfall JSON in, `mtg-sdk` models out, with a grammar where every
rule is written in both directions so it proves itself against the whole corpus without a human
reading the output.

Design: [`docs/oracle-assay.md`](../docs/oracle-assay.md) · Build order:
[`docs/plans/oracle-assay.md`](../docs/plans/oracle-assay.md)

**Phase 1 is implemented: the kernel, invertible normalization, the touchstone gate, and a grammar
covering vanilla cards and keyword-only abilities. The differential gate is live** over the class the
grammar reads whole, and the first band of the pipeline family is in — cardinals, a filter and target
vocabulary, the one-verb spell effects (draw, destroy, exile, tap, untap, return to hand), the counted
verbs (life, scry, surveil, damage, pump) and the **trigger prefix** — the event triggers and the step
triggers ("When ~ enters, …", "At the beginning of your upkeep, …"), which is where the differential
started comparing whole *abilities* rather than keyword lists, and where it found its first bug in a
hand-written card. The **land band** followed — mana abilities and "~ enters tapped." — the first
family where whole-card coverage moved with line coverage, because a land is a one-to-three-line card
and those are the two sentences on it. The **aura band** is the most recent: `Enchant <filter>` and
the attached-permanent statics, which opened `staticAbilities` — the largest `CardScript` slot the
differential could not see into, and the one every later static family lands in.
Nothing here changes `:mtgish-tooling`, which stays authoritative until a per-set cutover replaces it
(Phase 5). Assay is **not a runtime card loader** and never will be.

## Commands

```bash
just assay parse "Serra Angel"      # normalized lines + the SDK model each parses to
just assay explain "Wall of Omens"  # the same, with a caret on the token a decline died on
just assay-gate                     # the touchstone over the whole corpus; exit 1 on a bug
just assay-report --top 40          # the same numbers, always exit 0
just assay-report --scope           # restricted to Phase 1's own target class
just assay-report --implemented     # restricted to cards that already have a golden — the *grammar* backlog
just assay-differential             # Assay's readings vs. the hand-written cards
just assay-explore                  # all of the above in a browser, on the live grammar
just assay corpus --refresh         # re-download the Scryfall Oracle bulk (~24 MB, cached 7 days)
```

The corpus is Scryfall's `oracle_cards` bulk file, cached at
`~/.cache/scryfall/_bulk-oracle-cards.jsonl.gz` — the same directory `scripts/card-status` and
`:mtgish-tooling` use, under a `_bulk-` prefix that cannot collide with a set code. Scryfall serves
it as gzipped JSONL, so it streams a card at a time.

## Where things are

```
syntax/     Phrase kernel — templates, slots, both directions, memoization, the parse cap
normalize/  Scryfall text -> canonical ability lines, every pass with its inverse; reminder glosses
corpus/     the Scryfall Oracle bulk: download, cache, stream
grammar/    the rules, by topic — Primitives, Keywords, Cardinals, Filters, Targets, Steps, Triggers,
            Mana, Costs, Activated, Replacements, Statics
            Steps covers draw, destroy/exile/tap/untap/bounce, life, scry/surveil, damage, pump and
            adding mana; Activated is the cost-colon-effect sentence, which slots Steps whole;
            Statics is the continuous-ability slot, opened on the aura payoff lines
gate/       the touchstone, the fineness report, the differential
explore/    the browser UI — a loopback HTTP server over the live grammar and both gates
cli/        assay parse | explain | gate | report | differential | explore | corpus
```

## The three things that make this different

**The grammar runs backwards.** A rule registers `build` (text→model) and `match` (model→text)
together, and a canonical rule *cannot be constructed* with only one of them. That buys the gate:

```
print(parse(normalize(t))) == normalize(t)      // or: declined, and counted
```

**There is no Assay IR.** Rules parse straight into `mtg-sdk` types through the SDK's own
companion factories. So "can Argentum express this?" collapses into "did it parse?", and a decline
names a missing capability in Argentum's own vocabulary rather than in a third ontology's.

**Declining is success.** Unparseable text is counted and ranked by cards blocked, never
approximated. The bottom of the fineness report is a continuously-updated SDK gap report.

## Reading the verdicts

| Verdict | Meaning |
|---|---|
| `ROUND_TRIP` | Printed back byte-for-byte. |
| `VARIANT` | An alternate spelling normalized to the canonical one; reparsing the printed line gives the identical model, so nothing was lost — only the spelling moved. |
| `MISMATCH` | Printed something the grammar does not read back the same way. **A bug. Must be 0.** |
| `AMBIGUOUS` | Two rules, two different models, one text. **A bug. Must be 0.** Never resolved by picking one. |
| `DECLINED` | Not covered. Counted, ranked, and named — not a bug. |

`MISMATCH`, `AMBIGUOUS`, and a non-invertible normalization pass are what `just assay-gate` exits
non-zero on. Declines are not failures.

## Phase 1 results (whole corpus, 2026-08)

```
Cards assayed                    34882
Ability lines                    66793  (39059 unique)

Round-trips byte-exact           17977   269.1‰ (26.9%)
Alternate spelling normalized    30
Declined                         48786
Ambiguous — distinct readings    0
Print mismatch                   0
Normalization not invertible     0
Full inverse not reproduced      0

Cards fully covered              3004 / 34882   86.1‰ (8.6%)
Vanilla + keyword-only cards     1440 / 1712   841.1‰ (84.1%)   <- Phase 1 target
Reminder-text glosses            2870 matched · 114 differed · 956 unglossed
```

Fineness is **parts per thousand**, per the assay the module is named for — 841.1‰ is 84.1%.

The machinery holds: **zero** ambiguities, print mismatches, or non-invertible normalizations
across 66,793 ability lines. The 84.1% on Phase 1's own target class is not the round trip
faltering — every remaining line in that class declines because the SDK has no vocabulary for the
keyword, which `just assay-report --scope` lists in rank order.

## What Phase 1 already found

The report is two documents at once, and the second one is about `mtg-sdk`:

- **Two keyword abilities of identical shape are modelled two different ways.** `Enchant creature`
  (1,289 cards) is an aura's attachment restriction and `Equip {2}` (621 cards) is a
  `CardDefinition.equipCost` field — neither is a `KeywordAbility`, so neither had anything to
  parse *into*. They were the two largest keyword-only decline families in the corpus.
  **Only one of them turned out to be a sentence.** Enchant is a plain `TargetRequirement` in a
  plain `CardScript` slot, so the aura band reads it as an ordinary filtered target and the whole of
  `Filters` arrives with it — "Enchant land" and "Enchant creature you control" are rows in a list,
  not rules. Equip is not the same shape at all: it lowers at authoring time into `equipCost` *and*
  a synthesized activated ability with its own timing, effect and target requirement, so reading it
  means reproducing a lowering rather than a sentence, and it reaches past `CardScript` into a slot
  `CardFragment` does not model. That is why it is still declined, and why the pair stopped being
  one finding.
- **`PROTECTION_FROM_EACH_OPPONENT` and `ProtectionScope.EachOpponent` are two spellings of one
  thing.** Registering both would be genuine ambiguity, so the grammar deliberately spells it only
  one way (see `Primitives.protectionScope`).
- **The printed separator is not recoverable from the model.** ~31 older cards print
  "Flying; banding"; a flat `List<KeywordAbility>` has no room for the separator, so they come back
  as `VARIANT`. Same class as line grouping, which normalization owns instead.
- **Reminder text is a function of the ability *and* the card's types.** Printed glosses say "this
  creature" / "this artifact" / "this land", which a `KeywordAbility` alone cannot produce —
  `Reminders.gloss` takes the noun as a parameter for exactly that reason.
- **~40 keyword abilities have no `Keyword` enum constant at all** — Exalted, Infect, Echo,
  Soulshift, Bloodthirst, Scavenge, Backup, Megamorph, Unleash, Extort, Evolve, Myriad, Unearth,
  Mentor, Afterlife, Enlist, Champion, Eternalize, Skulk, Melee, Battle cry, Reinforce, Devoid,
  Dethrone, Phasing, Cumulative upkeep, … — ranked by cards blocked in the report's bottom table.

## The differential gate

`just assay-differential` diffs Assay's reading of a card against the `CardDefinition` a human wrote
from the same text. The touchstone proves a parse is *reversible*; this is the gate that asks whether
it is *right*, and it runs on an asset the incumbent pipeline structurally could not have — the
committed card goldens under `mtg-sets/src/test/resources/snapshots/cards/`, decoded through
`mtg-sdk`'s own `CardLoader`. Reading them is a file read, so the SDK-only dependency rule holds.

**Scoping is fail-closed.** A card is compared only where Assay reads *every* line of it. Comparing a
partially-read card would count a keyword Assay never saw as agreement, so everything else lands in a
named population bucket instead and the denominator stays visible.

```
  Hand-written cards                 8874
    compared                         930
    not yet covered by the grammar   7342
    script slot not modelled yet      39
    lines do not fold into one card   20
    multi-face (out of scope)        301
    Oracle text differs from golden  242
    golden would not decode            0

  Confirmed — models agree           924   993.5‰ (99.4%)
  DIVERGENT — read every one           6
```

The divergence count is not meant to stay at zero — it rises every time the grammar reaches a new
class of card, and each rise is the gate earning its keep. The five it opened with, the eight the
first band of spell rules produced, and the six the land band produced are all fixed or classified
below. The aura band is the first new card class that added **none**: the 40 auras it brought into
the population all agreed, and a by-hand sweep of every golden printing one of those three sentences
found no disagreement either. That is a fact about the cards rather than about the gate — an aura in
this band is two lines with nothing to drop, where the bugs the gate has found were all a clause
lost *inside* a filter on a longer sentence.

Five separate things have to hold before a card is compared, and each has its own bucket. Every one
of them was added after the gate was caught claiming a check nobody had performed:

| Guard | Why |
|---|---|
| Assay reads every **line** | A keyword whose line declined would look like agreement. |
| The text is the **same text** | A golden carries the wording it was authored from; if that is not what Scryfall serves, Assay is reading one card and diffing another. Compared normalized, so inconsistently-included reminder text is not a difference. |
| The definition uses only **modelled slots** | A keyword the SDK lowers to a triggered ability at authoring time leaves content the grammar cannot produce. Confirming it would claim a check nobody performed. |
| The lines **fold into one card** | A `CardScript` has one `spellEffect`; a card printing two effect paragraphs means a sequence the grammar has no rule for. Neither keeping the first nor concatenating them is honest, so it is counted. |
| The card has no **unread abilities** | A keyword the SDK lowers at authoring time puts an ability in the script that no text line prints — prowess, provoke, rampage, training and mobilize become triggers; cycling, equip, morph and level up become activated abilities. A card carrying more of either than Assay read is carrying content nobody printed. One-directional: *Assay* having more would mean the grammar invented an ability, and that must diverge loudly. |

A divergence never fails the build — it is a finding to classify as **parser bug**, **card bug**, or
**fold**. Only an undecodable golden exits non-zero. The fold list lives in `gate/Differential.kt`
and is reviewed rather than grown silently: every entry is a divergence the gate stops reporting, so
each one has to say why it is not a difference.

### What the gate has found

- **A bug in a hand-written card — the outcome this whole thing is for.** Meteor Golem's printed text
  is "destroy target nonland permanent **an opponent controls**"; its definition filtered on
  `TargetFilter.NonlandPermanent`, so the golem could be pointed at its own controller's board and
  the engine offered those permanents as legal targets. A generated render that dropped a clause,
  committed and unnoticed. Fixed with a scenario test asserting the negative — a permanent you
  control is **not** in `legalTargets` — which is the half that fails without the fix.
  This is also the answer to "why not just diff the printed text": nothing about the card *looked*
  wrong, and its own `oracleText` field carried the clause it did not implement.
- **Two more of exactly that class, from the land band's first run.** Opening `activatedAbilities`
  took the compared population from 653 cards to 890 and immediately found both:
  **Voltaic Construct** prints "{2}: Untap target **artifact creature**" and filtered on
  `TargetFilter.CreatureOrArtifact` — an `Or` where the text is a conjunction, so it untapped any
  creature *or* any artifact, a strictly larger set than the card allows. **Dwarven Miner** prints
  "{2}{R}, {T}: Destroy target **nonbasic** land" and filtered on `TargetFilter.Land`, so it destroyed
  basic lands. Both are generated renders that dropped a clause, both were committed with their own
  `oracleText` carrying the clause they did not implement, and both are fixed with a scenario test
  asserting the *negative* — the permanent the text excludes is not in `legalTargets` — which is the
  half that fails without the fix. Three such bugs in three new card classes is the pattern the gate
  predicted: a divergence appears the first time the grammar reads a class, not later.
- **A parser bug of exactly the class this gate exists for — fixed.** Assay read "protection from
  black and from red" as one `Protection(Colors([BLACK, RED]))`; the cards spell it as two
  `Protection(Color)` abilities. **The cards were right** — CR 702.16g: *"'Protection from [quality A]
  and from [quality B]' … behaves as two separate protection abilities."* The reading round-tripped
  perfectly and meant the wrong thing, so the touchstone could never have caught it. It affected
  Paladin en-Vec, Sabertooth Nishoba and Akroma, Angel of Wrath. The fix is `Keywords.qualityRun`: a
  rule that denotes *several* abilities from one phrase, which is why a keyword line now parses as a
  list of **groups**. It generalized while it was at it — the join is over any quality, not just
  colours, and CR 702.11f gives hexproof the same shape, so "protection from Demons and from
  Dragons", the Oxford-comma three-way, and "hexproof from white and from black" all read now.
  `ProtectionScope.Colors` is consequently a scope the grammar never emits.
- **A second SDK spelling that could not have worked — deleted.** `KeywordAbility.Flanking` (a
  `data object`) and `Simple(Keyword.FLANKING)` both existed and were not equal; the cards used the
  second and the grammar emitted the first. The engine reads flanking off the *projected keyword
  set* (`TriggerAbilityResolver` synthesizes the trigger for anything with `Keyword.FLANKING`), and
  the object overrode no `keyword`, so it never reached `CardDefinition.keywords` — a card authored
  with it would have printed "Flanking" and done nothing. No card used it. It is gone from `mtg-sdk`.
- **Two implementations of Affinity in the corpus.** Frogmite spells it `KeywordAbility.Affinity`;
  Qumulox, Memory Guardian and the five Darksteel golems hand-roll the same text as a
  `ModifySpellCost` static ability. Both work — this is an inconsistency, not a bug — but it is the
  same "one concept, two spellings" family, and it is why those cards sit in the
  `script slot not modelled yet` bucket rather than being compared.
- **The gate lying to itself, for the third time.** The slot-name normalization — which exists
  because the string linking a requirement to the effect reading it is arbitrary — was a textual
  replacement over the serialized script, and the grammar's slot is called `target`, which is *also*
  the name of a field on every targeted effect. So `"target":{…}` was rewritten to `"slot_0":{…}` on
  Assay's side and left alone on any card that had named its slot something else, and six cards
  reported as divergent over a difference that was in neither model. It now walks the JSON tree and
  rewrites only `id` / `name` *values*. Every one of the three has been the gate finding a way it
  could have lied; none was found by reading the code.
- **The gate lying to itself, for the fourth time — the slot fold was scoped to the wrong thing.**
  The positional-reference fold below numbers a script's target slots so `ContextTarget(0)` and a
  named requirement compare equal, and it numbered them *card-wide*, starting at the root. But a
  `ContextTarget`'s index counts within its own **owner** — a `CardScript`, a `TriggeredAbility` and
  an `ActivatedAbility` each declare their own requirements — and a card-wide counter that never
  descended into an ability simply stopped. It agreed with every card for as long as the grammar
  produced only top-level requirements; Trench Wurm, whose whole script is one activated ability with
  a positional target, is what a card looks like when it stops agreeing. Numbering is now per owner.
  The pattern holds: each of the four was found by *running* the gate on a new card class.
- **A positional target reference and a named one — folded, with the SDK's own words for it.**
  Murderous Compulsion and Ureni's Rebuff refer to their target as `ContextTarget(0)` against an
  unnamed requirement; Assay always mints a name. The SDK documents `BoundVariable` as "safer and
  more self-documenting than `ContextTarget(index)`" — the same link written by name instead of by
  position — so the comparison now normalizes both to the requirement's *position*, and what it
  compares is which requirement an effect reads. `ContextTarget(1)` still diverges from anything
  reading slot 0.
- **Open, and not folded: `TargetCreatureOrPlaneswalker` versus the general filtered target.** Hero's
  Downfall spells "target creature or planeswalker" as a dedicated requirement type; 29 other card
  sites spell it as `TargetObject(CreatureOrPlaneswalker)`, and 219 cards in the corpus print the
  phrase. Both are fully wired, down *parallel* code paths — `TargetFinder` hand-rolls the
  hexproof / shroud / can't-be-targeted checks separately for each. That parallel implementation is
  the reason this is not folded: the two agreeing today is an accident of two code paths, not a
  stated equivalence, and folding would stop the gate from noticing if they drift. Unifying them is
  an SDK cleanup with 17 card sites behind it. The damage rules added a second instance — Sear —
  which is what a standing finding is supposed to do: it recurs, in a new sentence shape, unchanged.
- **Open: "you" has two spellings, and asymmetric facade defaults are why.** Nightdrinker Moroii
  writes "you lose 3 life" as `EffectTarget.PlayerRef(Player.You)`; 116 other sites write it as
  `EffectTarget.Controller`, against 20 for the `PlayerRef` form, and the grammar emits the majority.
  Both resolve to `context.controllerId` in the *same* `when` in `TargetResolutionUtils`, so nothing
  is broken — but they are **not** interchangeable in general: the entity resolver in the same file
  handles `Controller` and falls through to `null` for `PlayerRef(You)`. That asymmetry is why this
  is not folded; a fold here would have to be scoped to player-directed effects to be true.
  The mechanism that produced the split is worth naming: `Effects.GainLife` defaults its target to
  `Controller` while `Effects.LoseLife` defaults to `PlayerRef(Player.TargetOpponent)`, so an author
  writing "you gain" takes the default and one writing "you lose" must override — and reaches for the
  shape the signature showed them.
- **Still open: `ProtectionScope.Colors` is one of those spellings.** CR 702.16g defines the joined
  text as two abilities, which is how all but one card in the corpus writes it — Ureni, the Song
  Unending uses `Colors`. The scope is engine-supported (`CardEntityFactory`, `PlayerProtectionRules`)
  so nothing is broken; it is one card and one type away from the corpus having a single spelling.
- **Open: a mana ability says so twice, and 24 abilities say it once.** `ActivatedAbility` carries
  `isManaAbility: Boolean` *and* `timing: TimingRule.ManaAbility`, and `TimingRule.ManaAbility`'s own
  KDoc claims the rules meaning — "does NOT go on the stack (Rule 605.3a)", "can be activated during
  mana payment even without priority" — that the engine actually implements off `isManaAbility`
  everywhere. 620 hand-written mana abilities set both; 24 set only `isManaAbility`, because
  `activatedAbility { manaAbility = true }` sets that flag and leaves `timing` at its `InstantSpeed`
  default. The grammar derives both from CR 605.1a and emits the majority, so Bog Initiate, Wirewood
  Elf and Elvish Aberration diverge. Nothing is broken — every engine read is on `isManaAbility`, and
  the one site that tests `timing == InstantSpeed` (the AI's `ExpiringGrantWindow`) returns early on
  `isManaAbility` first — but this is **not folded**, because the two fields agreeing is a property
  of how cards happen to be authored rather than a stated equivalence, and an ability with
  `timing = ManaAbility` and `isManaAbility = false` would print as a mana ability and use the stack.
  Folding would stop the gate noticing that.
- **Open: "enchanted creature", "enchanted land" and "equipped creature" are one model.**
  `GroupFilter.attachedCreature()` is `GameObjectFilter.Permanent` scoped to `AttachedTo` — it says
  *the thing this is attached to* and nothing about that thing being a creature, or about the
  attachment being an Aura. So all three printed forms denote the identical value, and registering
  more than one rule for it would be genuine ambiguity: several printed forms, one model, nothing
  for the printer to choose. The grammar spells exactly one, the noun nearly every card uses, and
  the others decline. This is not an SDK gap — the model is right, and which word a card prints is a
  function of its *type line*, the same class of printed-shape information as the self-reference
  noun ("this creature" vs "this Equipment") that `Normalizer` already records and restores. When
  the equipment forms are read, they belong in that pass and not as a second rule.
- **`mtg-sdk` has no `Statics` facade.** `dsl` publishes `Effects`, `Triggers`, `Costs` and
  `Conditions`; static abilities have nothing, and hand-written cards construct them directly
  (`staticAbility { ability = ModifyStats(1, 2) }`). The constructor is the curated surface here, so
  `Statics` builds through it — the same situation `Replacements` is in with `EntersTapped`. Two
  facades missing for the two ability kinds that never got one is a small, consistent SDK finding.
  Related, and worth knowing before reading a golden: **two SDK types share
  `@SerialName("ModifyStats")`** — the `StaticAbility` and `ModifyStatsEffect`, the effect behind
  "Target creature gets +3/+3 until end of turn." Different polymorphic hierarchies, so nothing
  clashes, but one card's JSON can show both under one type name.
- **Open: `ManaColorSet.Specific` is a second spelling of a dual land's line.** 165 cards write
  "{T}: Add {B} or {G}." as two `AddManaEffect` abilities sharing a cost, and 13 write it as one
  `AddManaOfChoiceEffect(ManaColorSet.Specific(...))`. Unlike the other entries in this list the
  split has a *reason*: every card in the smaller group carries a rider the two-ability form cannot
  express correctly — "Activate only once each turn" on two abilities permits two activations — so
  the type earns its place. The grammar emits the majority and never emits `Specific`, and none of
  the 13 is compared today because each one's rider declines anyway.

And the gate paid for itself before its first report: writing it surfaced that "Plains"
de-pluralized to `Subtype("Plain")` — the "Elves" → `Elve` failure, live on the basic land types,
round-tripping perfectly the whole time. `Primitives.pluralSubtype` now ranks candidate readings
against the SDK's own type lists instead of guessing. Running it then surfaced the join and
slot-completeness holes above, each of which was the gate finding a way it could have lied.

## The explorer

`just assay-explore` serves the whole module in a browser: the fineness numbers, the ranked
declines with the cards behind each one, every card's reading beside its printed text, the wired
grammar, the differential, and a box for text that was never printed.

**It runs against the grammar on this classpath, not a snapshot of it.** That is the difference
between this and the [mtgish model explorer](https://github.com/i5jb/mtgish) it is modelled on: that
page had to precompute its data and ship the parser as WebAssembly, because the parser was Go in
another repository and the page could not call it. Assay is ours and already linked, so a rule you
just edited is one restart away from being re-measured, and a custom card runs the identical
[`Touchstone`] path a corpus card runs — normalization, self-reference abstraction, reminder
stripping and the invertibility check included — instead of an approximation of it.
`com.sun.net.httpserver` is in the JDK, so the SDK-only dependency rule is untouched.

Two things it shows that no CLI report does:

- **Which cards are behind a decline family**, and how many of those already have a hand-written
  golden. `assay report` ranks the families; clicking one is the backlog it names, split into the
  grammar gaps (answer already written, differential confirms it the moment it parses) and the
  possible SDK gaps.
- **Both rankings side by side.** Keying declines by the token a line *died on* answers "what is the
  grammar missing"; keying them by the **sentence shape** — the line with numbers and mana symbols
  collapsed — answers "what should I write next", and they disagree sharply. The token ranking's top
  row is `Whenever` at 5,070 cards, which names no piece of work; the shape ranking's is
  `Enchant creature` at 921 cards, which is one rule.

The corpus sweep (~5s) runs in the background at startup, so the live parser and the rule tree are
usable before the numbers land; the differential runs on first request and is then cached, because
it decodes 8,874 goldens and most sessions never open it.

**Rule usage numbers are exact rather than indicative.** The kernel records no parse provenance — a
reading is a value, and the rule that produced it is gone by the time the gate sees it. But the
*print* side is deterministic: `oneOf` prints through the first canonical alternative that can
express the value, so "which rule printed this" has one answer, and it is the same walk the round
trip depends on. A rule showing no usage printed nothing in 34,882 cards.

## Adding a rule

1. Write it in `grammar/`, bidirectionally, through an SDK companion factory.
2. `just assay parse "<a card that uses it>"` — check the verdict, not just that it parses.
3. `just assay-gate` — the number that matters is that `MISMATCH` and `AMBIGUOUS` stay 0.
4. Add the surface form to `KeywordGrammarTest`'s round-trip list.

Three traps the kernel cannot catch for you:

- **A `match` half that quietly matches nothing** still compiles and still parses; it shows up on
  the corpus as a print mismatch far from its cause. The `every keyword rule can print what it
  parses` test exists for this.
- **Reversible but wrong.** "Elves" de-pluralizes to `Elve` and round-trips perfectly while meaning
  nothing. The touchstone structurally cannot catch that class — run `just assay-differential`, which
  is the general answer and has already caught two of these.
- **Templates are mid-sentence, and a line has more than one sentence in it.** Write `"draw a card."`
  and `"add {mana}."`, never `"Draw a card."` — `syntax/SentenceCase.kt` decapitalizes the line's
  first word *and* the clause after each ability cost's `": "`, then recapitalizes both on the way
  out. That is what lets `{T}: Add {G}.` be `Costs.cost` plus an unmodified `Steps` rule instead of a
  second, capitalized copy of the effect vocabulary.
