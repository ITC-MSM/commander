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
and those are the two sentences on it. The **aura band** followed: `Enchant <filter>` and the
attached-permanent statics, which opened `staticAbilities` — the largest `CardScript` slot the
differential could not see into, and the one every later static family lands in.

The most recent work is the **counters band**, and it is the first one picked by *ranking the
backlog* rather than by picking a set: `just assay-report --implemented` says 656 cards with a
hand-written golden decline on nothing but a counter sentence, which is the largest sole-blocked
family in that population. See [the counters band](#the-counters-band) below.

Before that came the **Legions band**, the second set read end to end and the first *hard* one: `just assay-gate --set LGN` reads **145 of Legions' 145 cards**. Legions is every-card-a-
creature, so it is a set made almost entirely of the things Portal had none of — morph payoffs,
tribal lords, granted abilities, amplify, counted variables — and reading all of it took nine new
families rather than nine new rules. See [the Legions band](#the-legions-band) below.

Before it came the **Portal band**, the first one measured against a whole *set* rather than against
a rule family: `just assay-gate --set POR` reads **200 of Portal's 200 cards** end to end. Getting there was not two hundred rules — it was the machinery a set needs before any of its
cards can be read whole. **A line is a sequence of clauses**, so a card printing two sentences reuses
the effect vocabulary twice instead of restating it capitalized; **a noun phrase is a layered cascade
in two grammatical numbers**, so "black creatures you control" and "creatures with power 2 or
greater" are compositions rather than rules; **two anaphors** ("that creature", "it") are separate
vocabularies because they point at different objects; and **three restriction vocabularies** —
casting, activation, additional costs — reach the `CardScript` slots that say when a card may be
played rather than what it does. Nearly everything else was rows in those lists. Whole-corpus
coverage went from 3,004 cards to 4,287 in the same change, which is the argument for picking a set
as the target rather than picking the number.

Nothing here changes `:mtgish-tooling`, which stays authoritative until a per-set cutover replaces it
(Phase 5). Assay is **not a runtime card loader** and never will be — with one carved-out exception,
the [custom-card sandbox](#the-compiler-and-the-custom-card-sandbox), which compiles a *pasted* card
for a dev-gated Scenario Builder session and never touches the corpus.

## Commands

```bash
just assay parse "Serra Angel"      # normalized lines + the SDK model each parses to
just assay explain "Wall of Omens"  # the same, with a caret on the token a decline died on
just assay compile "Serra Angel"    # the reading as a whole CardDefinition (JSON on stdout)
just assay compile --file card.json # …from a pasted Scryfall object — the custom-card path
just assay-gate                     # the touchstone over the whole corpus; exit 1 on a bug
just assay-report --top 40          # the same numbers, always exit 0
just assay-report --scope           # restricted to Phase 1's own target class
just assay-report --implemented     # restricted to cards that already have a golden — the *grammar* backlog
just assay-report --set POR         # restricted to one set — every card *printed* in it
just assay-differential             # Assay's readings vs. the hand-written cards
just assay-explore                  # all of the above in a browser, on the live grammar
just assay corpus --refresh         # re-download the Scryfall Oracle bulk (~24 MB, cached 7 days)
```

The corpus is Scryfall's `oracle_cards` bulk file, cached at
`~/.cache/scryfall/_bulk-oracle-cards.jsonl.gz` — the same directory `scripts/card-status` and
`:mtgish-tooling` use, under a `_bulk-` prefix that cannot collide with a set code. Scryfall serves
it as gzipped JSONL, so it streams a card at a time.

**`--set` is membership, and it has to be.** One object per Oracle ID means each card carries a
single *representative* printing, so its `set` field says which printing Scryfall shows it under —
not which sets it was printed in. Filtering on that field showed **53 of Portal's 200 cards**: Blaze
is shown as `bbd`, Raise Dead as `w17`, Wild Griffin as `cn2`, and Portal's own reprints of older
cards are credited to the older set, so a set loses cards in both directions. `--set` and the
explorer's set box therefore ask Scryfall for the set's card list and join on Oracle ID
(`corpus/SetMembership.kt`), cached per set at `~/.cache/scryfall/_setlist-<code>.tsv`. Per set
rather than one global index because the filter is always one set at a time — a `default_cards`
Oracle-ID → sets map costs a 77 MB download to answer a ~200 KB question. A set that cannot be
resolved matches **nothing** and says so; degrading to the whole corpus would be a report lying about
its own population. The Set *column* still shows the representative printing, and is labelled as such.

## Where things are

```
syntax/     Phrase kernel — templates, slots, both directions, memoization, the parse cap
normalize/  Scryfall text -> canonical ability lines, every pass with its inverse; reminder glosses
corpus/     the Scryfall Oracle bulk: download, cache, stream; per-set membership for `--set`
grammar/    the rules, by topic — Primitives, Keywords, Cardinals, Conditions, Filters, Targets,
            Steps, Continuations, Triggers, Mana, Costs, Activated, Replacements, Statics,
            Restrictions, and the effect-topic files Library, Hand, Combat, Graveyard, Stack,
            SelfSteps
            Steps is the clause vocabulary and the sentence/sequence machinery every other file
            slots into; Activated is the cost-colon-effect sentence; Statics is the continuous-
            ability slot; Restrictions is the three "when may this happen" vocabularies;
            Continuations and SelfSteps are the two anaphors ("that creature" / "it")
gate/       the touchstone, the fineness report, the differential
compile/    a whole reading as a CardDefinition — the custom-card sandbox's engine
explore/    the browser UI — a loopback HTTP server over the live grammar and both gates
cli/        assay parse | explain | compile | gate | report | differential | explore | corpus
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
Ability lines                    66793  (38943 unique)

Round-trips byte-exact           22940   343.4‰ (34.3%)
Alternate spelling normalized    1114
Declined                         42739
Ambiguous — distinct readings    0
Print mismatch                   0
Normalization not invertible     0
Full inverse not reproduced      0
Redundant readings (same model)  0

Cards fully covered              6335 / 34882   181.6‰ (18.2%)
Vanilla + keyword-only cards     1444 / 1712   843.5‰ (84.3%)   <- Phase 1 target
Portal (set POR)                 200 / 200     1000.0‰ (100%)   <- the Portal band's target
Legions (set LGN)                145 / 145     1000.0‰ (100%)   <- the Legions band's target
Reminder-text glosses            2870 matched · 114 differed · 965 unglossed
```

Fineness is **parts per thousand**, per the assay the module is named for — 841.1‰ is 84.1%.

The machinery holds: **zero** ambiguities, print mismatches, or non-invertible normalizations
across 66,793 ability lines. The 84.1% on Phase 1's own target class is not the round trip
faltering — every remaining line in that class declines because the SDK has no vocabulary for the
keyword, which `just assay-report --scope` lists in rank order.

**A whole set at 1000‰ is worth more than the percentage it moved.** A rule family's coverage number
says how much of one shape the grammar reads; a *set* is an arbitrary cross-section of Magic, so
reading all of one means the grammar has no systematic hole in that era rather than no hole in that
family. Portal is a deliberately simple set, which is what makes it the right first one — and the
318 alternate spellings above are mostly its doing, because a card printing "A and B" or "A, then B"
now reads correctly and prints back as the full-stop form.

## The Legions band

Legions is 145 cards and every one of them is a creature, which makes it the opposite of Portal in
exactly the way that is useful: Portal proved the grammar could read a set of *spells* with simple
sentences, and Legions proves it can read a set whose sentences are all about permanents that grant,
count, transform and tax. Reading all of it needed nine pieces of machinery and, after those, rows.

**Subtypes, and the case problem they exposed.** A tribal set is built out of one noun phrase —
"Sliver creature", "non-Zombie creature", "Bird and/or Cleric permanent" — so `Filters` grew a
subtype layer, its negation and its disjunction. That immediately hit something the grammar had
never met: a subtype is a **proper noun**, and `SentenceCase` lowercases every sentence start
before the grammar sees it. "Sliver creatures get +1/+0." is a whole line and
"{T}, Sacrifice a Goblin: Goblin creatures get +2/+0 …" starts a second sentence after the colon, so
undoing the lowercasing at the text boundary would mean guessing which of a line's sentence starts
were proper nouns. It belongs to the leaf instead: `Primitives.subtype` reads the lowercased
spelling, and **only for a word the SDK names as a type**, so nothing is guessed and no common noun
acquires a second reading.

That gate is the whole rule, and the differential proved why. An earlier attempt retried the line
*as printed* when the ordinary reading declined — which reads position 0 with its capital intact, and
therefore reads "**Other** creatures you control get +0/+1." as creatures of a type called *Other*.
Byte-perfect in both directions, and about a tribe Magic does not have. It bought a few dozen cards
whose first word is a subtype nobody published a list for; declining those is the better half of that
trade, and "Other" is now a real prefix on the lord rules instead.

**Costs are an atom vocabulary.** `{2}{B}, {T}, Sacrifice a Goblin` is three costs, so `Costs`
became a list of atoms plus one comma-joined run — and `{1}, {T}`, which used to be a rule, stopped
being one. A cost is also the single clause Oracle capitalizes that is **not** a sentence start:
"Sacrifice a Goblin: …" is lowercased by the case pass at a line start and left alone after a comma,
so each verb atom is instantiated twice from one template and only the capitalized instance prints.

**Lords, and grants in both of their printed shapes.** "Sliver creatures get +1/+0." is one shape
with three members — a stat modifier, a keyword, or a whole quoted activated ability — over a group
`Filters` names. The quoted grant slots `Activated` unchanged, which is what makes
"All Slivers have "{T}: Regenerate target Sliver."" a row rather than a rule. The *unquoted* grant is
its twin: "Whenever a Sliver deals combat damage to a player, its controller may draw a card." is a
`GrantTriggeredAbility` whose noun lands on the grant and never on the event, and whose subject is
"its controller" because the sentence is written from the granting card's point of view rather than
the gaining permanent's. Those third-person clauses are reachable **only** from a grant, and that is
deliberate: outside one, "its controller" means the controller of whatever the sentence was just
talking about, so a global spelling would misread "Whenever a creature dies, its controller loses
1 life" as a sentence about you.

**Counted variables.** "…gets +X/+X until end of turn, where X is the number of Elves on the
battlefield" defines its number in a trailing clause, "…equal to the number of +1/+1 counters on it"
in a prepositional one, and "…for each +1/+1 counter on it" in a third. All three put a
`DynamicAmount` where a numeral would go, so `Amounts` is one file with a count vocabulary and the
verbs that slot it.

**Morph.** The keyword was already a `Keywords` row; the band added the trigger it pays off
("When ~ is turned face up, …", 27 of the set's cards) and the effects that turn other permanents
over. Amplify came with it, and it is the one line in the grammar whose two halves land in **two
different card slots**: the SDK spells it as a bare `Keyword.AMPLIFY` plus an
`EntersWithRevealCounters` carrying the printed number, so the line denotes a keyword *and* a
replacement effect and there is no `Numeric(AMPLIFY, n)` anywhere in the corpus.

**Two smaller pieces with wide reach.** A trigger line can denote **several** abilities, because
"Whenever ~ attacks or blocks" is two events with one payoff. And a sequence's target is declared at
its first mention, which is not always the *first* clause — Fleshformer prints "~ gets +2/+2 and
gains fear until end of turn. Target creature gets -2/-2 until end of turn.", where the introducing
clause is second. `Steps` now finds the owning clause by printability rather than assuming index 0,
and at most one position can satisfy it, so the split stays deterministic.

Whole-corpus coverage went from 4,287 cards to 6,157 in the same change, and the differential's
compared population from 1,636 to 2,387 — which is again the argument for picking a set as the target
rather than picking the number.

## The counters band

Four sentences — "Put a +1/+1 counter on target creature.", the same clause aimed at the source
("…on ~.") and at the target an earlier clause chose ("…on it."), and the entry replacement
"~ enters with two +1/+1 counters on it." Whole-corpus coverage went 6,157 → **6,335 cards**, the
differential's compared population 2,387 → **2,431**, and its confirmed count 2,383 → **2,425**.

**It is the first band chosen by ranking the backlog rather than by picking a set, and the ranking is
the part worth reading.** The token table's top row was `you` at 595 implemented cards — a trigger
*subject*, which the step triggers already proved over-promises: 410 cards declined on "At the
beginning of…" and adding every prefix moved whole-card coverage by 23, because a line dies on its
first unknown token and a trigger's real blocker is usually after the comma. So the ranking that
decides work is **cards whose line dies at the verb**, where everything before it already read, and
by that measure counters are the largest family in the corpus: **1,025 implemented cards carry a
counter line the grammar could not read, and 656 of them decline on nothing else**. The verb also multiplies rather than adds —
`Triggers`, `Activated` and the modal rules all slot `Steps.step` whole, so one clause vocabulary
arrives in every context already wired. That is the opposite trade from a prefix, and the two
worked examples now sit either side of it.

**Two leaves, and both of them own a spelling no rule above them can see.** `Primitives.counterKind`
reads the noun and is gated on `CounterType.fromName` — the SDK's own answer to "is this a counter",
the same function `StatePredicate.HasCounter` parses with — because the model field is a bare
`String` and an ungated leaf would read *any* word as a kind and round-trip a counter Magic does not
have. That is `creatureSubtype`'s argument, and the "Elves" → `Elve` failure it exists to prevent.

The second leaf is the **indefinite article**, and it is inside the leaf for `statModifiers`' reason:
English picks "a" or "an" from the sound of the next word, so two rules — one per article — would
leave printing undetermined by the model, which is invariant 2 rather than a preference. It can be
one leaf because the corpus states the rule without an exception: across all 34,882 Oracle texts
**no counter kind is ever spelled both ways** — 223 kinds take "a", 38 take "an", disjoint. The
letter rule predicts all but three ("an hour", "an hourglass", "a unity"), only `hourglass` is a kind
the SDK names, and `token` re-reads what it writes on every call, so a wrong article could not
survive a corpus run.

**A two-word kind needed a lookahead, and the reason is a kernel property rather than a grammar
one.** "first strike" and "double strike" are counter kinds, and a leaf reads exactly *one* regex
match — `token` does not retry a shorter one when the gate rejects — so a greedy second word swallows
the template's own "counter" and declines every single-word kind. The noun's pattern therefore spells
out what it cannot be. Worth knowing before writing any leaf that can span a space.

**The band's real risk was the anaphor, and the machinery for it already existed.** "Put a +1/+1
counter on it" means the source in "Whenever ~ attacks, put a +1/+1 counter on it" and the *target*
in "Tap target creature an opponent controls and put a stun counter on it" — both readings round-trip
byte-perfectly, so nothing but the split could tell them apart. `SelfSteps.putCountersOnSelf` and
`Continuations.putCountersOnThatPermanent` are reachable from disjoint positions, exactly as
`SelfSteps.anaphoric` and `Continuations` have been since the differential caught "Untap target
creature. It gets +2/+4" meaning the wrong creature. This is the second sentence to need both, which
is the evidence that split generalized rather than patched one card.

**What the gate found: 44 newly-compared cards, 42 of them confirmed, and 2 divergences that are one
finding.** Landing on top of the divergence sweep is what makes that legible — against a baseline of
4 the band's own contribution is readable directly, where against 122 it would have been noise.

- **A card says "another" where its text says "an" — 8 cards.** Donatello, Way with Machines and
  Mm'menon, Uthros Exile both print "Whenever **an** artifact you control enters" and are authored
  with `binding = OTHER`, which excludes the source. The corpus maps the two spellings correctly
  almost everywhere — 33 cards print "an" and bind `ANY`, 40 print "another" and bind `OTHER` — so
  these are the exceptions rather than a convention, and a grep finds six more: Rimefire Torque,
  Airbender Ascension, Path of Discovery, Gossip's Talent, Death Match and Mana Echoes.
  **It is unobservable on all eight today**, and saying so is the honest half: every one of them is
  an enchantment or artifact triggering on a creature entering, so the source can never *be* the
  entering permanent and the binding never gets to matter. It is latent rather than harmless — an
  effect that makes Donatello an artifact as it enters is all it takes — and it is **not folded**,
  because folding would stop the gate noticing the first card where the binding is observable.

That is the whole of it: both new divergences are that one finding, and nothing else the band
brought into the population disagreed. A third — Invigorating Boon, the "you may on a triggered
ability" spelling — was divergent when the band was written against the pre-sweep baseline and is
not any more, because the sweep fixed that family while this was in flight.

**No new bug in a hand-written card**, and that is worth recording beside the aura band's same
result. Every bug this gate has found — Meteor Golem, Voltaic Construct, Dwarven Miner, Recollect
and Eternal Witness, and the 22 the sweep turned up — was a clause lost *inside a filter on a longer
sentence*. A counter sentence is short and has one filter, which is the same reason the auras added
none: there is very little in it to drop.

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
  Hand-written cards                 9123
    compared                         2431
    not yet covered by the grammar   6056
    script slot not modelled yet      82
    lines do not fold into one card   50
    multi-face (out of scope)        301
    Oracle text differs from golden  203
    golden would not decode            0

  Confirmed — models agree           2425   997.5‰ (99.8%)
  DIVERGENT — read every one            6
```

Four of those six are what the **sweep** left standing, and the other two are the counters band's,
classified in its own section above. The sweep took the count from 122 to 4: every divergence the
gate had accumulated was read, classified as parser bug / card bug / fold, and acted on. What it
found, by kind:

- **A bug in the gate itself, and a flaky one.** `AbilityId.generate()` is a global counter and
  `encodeDefaults` is false, so kotlinx re-evaluates the default to decide whether to emit an `id` —
  meaning a golden holding `ability_2` is omitted from the JSON exactly when the counter next returns
  `ability_2`. Two encodes of the same card differed, and the comparison was over the serialized
  *string*, so Blasting Station reported as divergent on some runs and confirmed on others.
  `Differential.sortKeys` compares objects as objects and closes the whole class.
- **22 hand-written cards that were wrong**, all but three of them unreviewed mtgish drafts, and all
  of them wrong in the way this gate is built to see — a clause dropped inside a filter. Fiery
  Cannonade hit every creature rather than every non-Pirate; Eyeblight Massacre every creature rather
  than every non-Elf; Magnetic Flux pumped artifacts *or* creatures rather than artifact creatures;
  Kangee's block trigger pumped every flier rather than every *blocking* flier; Joust Through gained
  3 life where the card says 1; Visara never stopped regeneration at all. Seven more read "sacrifice
  it unless you pay {G}{G}" as `PayCost.OwnManaCost` — the card's *printed* cost, which for the two
  lands among them is `{0}`, i.e. a sacrifice that never happens.
- **Four parser bugs**, each of the reversible-but-wrong class the touchstone cannot see: an
  intervening-if left duplicated in the effect as well as lifted into `triggerCondition`; Chromatic
  Sphere read as an instant-speed ability because the mana effect sat under a composite (CR 605.1a
  says "could add mana", not "does nothing else"); a two-pass reading of "creatures you control get
  +3/+3 **and** gain trample"; and two sentences printed in a spelling the corpus never uses.
- **One SDK finding acted on, one filed.** Acted on: `manaAbility = true` now derives
  `timing = TimingRule.ManaAbility` in `CardBuilder`, so the two spellings of one fact can no longer
  drift (24 cards carried only one, and the AI's `ExpiringGrantWindow` branches on `timing`).
- **Filed: the engine never performs CR 603.4's second intervening-if check.**
  `triggerCondition` is filtered at trigger *detection* and read nowhere else, so Beastbond
  Outcaster draws its card even when the 4-power creature was killed in response — against the
  ruling quoted in its own card file. Eight cards (Lavaborn Muse, Farsight Mask, Bloodhall Priest,
  Asylum Visitor, Heir of the Wilds, Convalescent Care, Oversold Cemetery) have noticed and
  hand-written a redundant resolution-time gate to compensate.

  A uniform recheck was implemented and **reverted**, because the field turns out to be overloaded
  three ways: 340 abilities use `triggerCondition` for an intervening-"if" (two checks), 47 for a
  "**while**" clause — "Whenever this creature attacks *while* you control a Dinosaur" is
  trigger-time only, and Burning Sun Cavalry and Seasoned Warrenguard have scenario tests asserting
  exactly that — and ~100 for other trigger-time restrictions. Rechecking all of them fails those
  two tests. The engine fix needs "if" and "while" separated in the SDK first, which is its own
  change with a corpus migration behind it.

The four that remain are classified, not unexplained:

| Card | Classification |
|---|---|
| Zombie Master | The bare-tribal-noun approximation, **measured**: "Zombies" means every Zombie *permanent*, and reading it that way costs 80 other cards (45 → 127 divergences). The one card in the corpus where the distinction is observable, because its granted ability says "Regenerate this permanent". |
| Lavaborn Muse | Carries the redundant resolution-time gate above. Correct under today's engine and divergent from the grammar; both stop being true when the "if"/"while" split lands. |
| Tattered Ratter | Parser bug. "it" inside a *filtered* trigger means the triggering creature, not the source — the third anaphor position. Fixing it needs a clause vocabulary reachable only from `filteredTriggerRule`, since `Primitives.self` and `SelfSteps.anaphoric` both build `EffectTarget.Self` today. |
| Kalastria Highborn | Parser bug. "You may pay {B}. If you do, A **and** B." — the outer clause-sequence rule takes the " and " join before the gate can, so B lands outside the gate. Fixing it by alternation order is what the design says never to do; the gate's scope has to run to the end of the sentence. |

The divergence count is not meant to stay at zero — it rises every time the grammar reaches a new
class of card, and each rise is the gate earning its keep. The five it opened with, the eight the
first band of spell rules produced, and the six the land band produced are all fixed or classified
below. The aura band is the first new card class that added **none**: the 40 auras it brought into
the population all agreed, and a by-hand sweep of every golden printing one of those three sentences
found no disagreement either. That is a fact about the cards rather than about the gate — an aura in
this band is two lines with nothing to drop, where the bugs the gate has found were all a clause
lost *inside* a filter on a longer sentence.

The **Portal band** took the compared population from 930 cards to 1,636 and the divergence count
from 6 to 30, which is the ratio the gate is supposed to have: six new cards read for every one that
disagrees. All thirty are classified below and they fall into six families, four of which are the
already-open "two SDK spellings of one thing" findings. Two are new bugs in hand-written cards, and
one was a bug in the parser that only this gate could have caught.

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

The **Legions band** took the compared population from 1,636 cards to 2,387 and the divergence count
from 30 to 122 — about nineteen new cards read for every one that disagrees, which is the ratio the
gate is supposed to have. All 122 fall into eleven families, and every family is classified below;
seven of them are the already-open "two SDK spellings of one thing" findings recurring in new
sentence shapes, three are new inconsistencies of the same kind, and one is a bug in a hand-written
card.

The band also found the gate lying to itself for the **fifth** time, and the first time in the
*other* direction: it reported six Sliver lords as divergent over an `AbilityId`. Ability ids are
canonicalized by position, but only in a card's top-level ability lists — and a `GrantTriggeredAbility`
carries a whole ability *inside* a static, one level further in. The gate was comparing a counter.
Found the way all five were, by running it on a card class it had never reached.

### What the gate has found

- **A card bug of the Meteor Golem class, from the Legions band.** **Flamewave Invoker** prints
  "{7}{R}: Flamewave Invoker deals 5 damage to **target player or planeswalker**" and declares a
  plain `TargetPlayer`, so the ability cannot be pointed at a planeswalker at all. Its own
  `oracleText` carries the clause it does not implement, which is the same signature every card bug
  this gate has found has had. Not fixed here — a card fix wants its own change and the scenario test
  that asserts the negative — but it is the highest-value thing in the band's output.
- **Legions' remaining 121 divergences, by family.** Each is a spelling difference the SDK permits in
  two ways, and the grammar emits one of them per the module's rule:
  - **A created token's art (19 cards).** `CreateTokenEffect` carries an `imageUri` no printed word
    determines; a rule that invented one would be inventing a URL. The text round-trips perfectly and
    the field is simply not in it.
  - **Mana-ability-ness (17).** The already-open finding, recurring: cards that set `isManaAbility`
    and leave `timing` at its `InstantSpeed` default. The band widened the *derivation* to match CR
    605.1a — "Add one mana of any color" and "Add three mana in any combination of {R} and/or {G}"
    are mana abilities as much as "Add {G}" is, and reading only the two symbol effects had made
    Blood Celebrant, Goblin Clearcutter and Wirewood Channeler instant-speed abilities that use the
    stack. Chromatic Sphere remains, because its mana step is inside a composite.
  - **"You may" on a triggered ability (~10).** `optional = true` versus a `MayEffect` wrapping the
    effect. *Since resolved by removing the flag from the SDK — see the closed finding below.*
  - **A mass effect written as a pipeline (~19).** `ForEachInGroup` versus a `Patterns.Group` recipe
    for the same sweep, and the already-documented `DealDamage(n, PlayerRef(Each))` versus
    `ForEachPlayer` split for "each creature and each player".
  - **A tribal noun's card type (~11).** "a Zombie card" is `Any.withSubtype(Zombie)` on Corpse
    Harvester and `Creature.withSubtype(Zombie)` in the grammar, which reads the bare noun as a
    creature — right in "target Sliver" and stricter than the text in "a Zombie card". The same split
    shows on the group sweeps whose filter omits `IsCreature`.
  - **`TargetCreatureOrPlaneswalker` versus the general filtered target (3).** The standing finding
    below, recurring in three new sentence shapes, still not folded and for the same reason.
  - **A `Gate.MayPay` cost's atom (6), a `GrantDynamicStatsEffect` holding a fixed bonus (3), a
    `descriptionOverride` (several), an explicit `fromZone` on a move that does not need one (2), and
    `ForceSacrificeEffect` versus `SacrificeEffect` for a bare "sacrifice a permanent" (1).** Each is
    one concept with two spellings and neither is broken; the grammar emits the one whose model says
    what the sentence says.
  - **Phage the Untouchable, on its own.** The band taught `Triggers` to read an intervening-if the
    way CR 603.4 defines it — a condition printed between the event and the effect is checked twice,
    so the ability carries both a `triggerCondition` and the `ConditionalEffect`. Lavaborn Muse is
    written exactly that way and now confirms; Phage carries only the `triggerCondition`, so its
    ability does not re-check on resolution. That is a card-side omission, not a spelling.
- **Two more bugs of the Meteor Golem class, from the Portal band.** **Recollect** prints "Return
  target card from **your** graveyard to your hand" and filters on `TargetFilter.CardInGraveyard`,
  which is *any* graveyard — so it can be pointed at an opponent's. **Eternal Witness** is the same
  card text inside an enters trigger and has the same filter. Elven Cache and Déjà Vu, the other two
  cards printing that sentence, both scope it with `ownedByYou()`, which is what makes these two a
  bug rather than a spelling. Both are generated renders that dropped a word, and both carry the
  clause they do not implement in their own `oracleText`. Fixed, each with the scenario test that
  asserts the *negative* — a card in an opponent's graveyard is not a legal target — which is the
  half that fails without the fix.

  A grep for the same filter found a third of the class the gate could not have: **Revive** prints
  "Return target **green** card from **your** graveyard to your hand" and filters on the unowned,
  uncoloured `CardInGraveyard`, so it ignores both words. It is not fixed here because the grammar
  declines its line — no rule reads a colour on a card noun — so the differential never compared it,
  and a fix wants the rule first so the gate can confirm it.
- **A parser bug of the reversible-but-wrong class — found, and fixed.** "Untap target creature. It
  gets +2/+4 until end of turn." read "it" as the *source* rather than as the target the first clause
  chose, because the same four words mean the source in "Whenever this creature attacks, it gets
  +2/+0". The line round-tripped byte-perfectly the whole time and meant a different creature, which
  is exactly what the touchstone structurally cannot see. The fix is the split between
  `SelfSteps.anaphoric` and `Continuations`: an anaphor resolves to the most recently mentioned
  object, so once a clause has introduced a target the pronoun is that target, and the two
  vocabularies are reachable from disjoint positions so no text has both readings. It affected
  Inspirit and Gerrard's Command.
- **"Deals N damage to each creature and each player" has two SDK spellings, and eight cards use the
  minority one.** `DealDamage(n, PlayerRef(Player.Each))` and
  `ForEachPlayerEffect(Player.Each, [DealDamage(n, Controller)])` are equivalent for a fixed amount —
  the second rebinds a controller the sentence does not need. Earthquake and Hurricane write the
  first and are confirmed; Fire Tempest, Howling Gale, Volcanic Spray, Magma Giant, Devastate,
  Hammerfist Giant, Rain of Embers and Steam Blast write the second and diverge. The grammar emits
  one, per the rule that two SDK spellings get one rule and a finding. Nothing is broken; it is one
  `Effects` helper away from the corpus having a single spelling.
- **A nested plain `CompositeEffect` is the same sequence as its flattening — folded, narrowly.**
  Cruel Tutor nests `Patterns.Library.searchLibrary` inside its outer composite and Bitter Revelation
  splices the same recipe's steps into a flat one, for the same printed sentence; Angelic Blessing's
  "gets +2/+2 and gains flying" is a two-element composite alone and three flat elements once a
  "Scry 1." follows it. A composite with `stopOnError` false and no description override is an
  ordered run and nothing more, so the two are one value written two ways. The fold splices only that
  shape and never reorders, so `[a, [c, b]]` still disagrees with `[a, b, c]`.
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
- **Closed, by deleting the field: a trigger's "you may" said itself twice.** `TriggeredAbility`
  carried an `optional: Boolean` beside its effect, and 106 cards used it where 214 wrapped the
  effect in a `MayEffect` — one sentence, two SDK spellings, bridged here by a `liftTriggerConsent`
  fold. The fold's own justification was the argument for removing the flag: it cited
  `TriggerProcessor.putOnStack` *building* `GatedEffect(Gate.MayDecide, then, otherwise)` from the
  flag on every game, which is a lowering, not an equivalence someone asserted. So the flag went and
  the gate is the model; `optional = true` survives only as a DSL shorthand that lowers in `build()`.
  Both halves of `Triggers.abilityFor`/`scriptFor` lost their lowering, `Granted` lost the same three
  lines, and the fold was deleted. The divergence count did not move, which is what proves the fold
  was folding nothing but the spelling. Two further conflations came out with it, both engine-side:
  a targeted "you may" used to carry its consent by forcing every target slot's minimum to zero
  (so "target creature" silently became "up to one", against CR 603.3d), and the single-legal-player
  target auto-select had to be disabled for optional abilities to stop that consent being skipped.
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

## The compiler and the custom-card sandbox

`assay compile` takes a reading the whole way: Scryfall JSON in, a `CardDefinition` out.

```bash
just assay compile "Serra Angel"       # a corpus card
just assay compile --file card.json    # a card that has no Scryfall entry at all
```

The Scenario Builder is where that becomes useful. Its **Custom cards** panel (dev endpoints only)
takes a pasted card object, shows what Assay read — each printed line with its verdict, the
canonical spelling where the author wrote a legal variant, and the caret on the token a decline died
on — and then lets you put the compiled card into any zone and *play* it. The question Assay was
built to answer becomes something you can hold: **is this card expressible in Argentum's vocabulary,
and what exactly does it say?**

Four constraints keep this from being the card loader this module refuses to be, and all four are in
code rather than in a convention:

- **Dev-gated.** `AssayCardService` reads `game.dev-endpoints.enabled`, and the player-facing
  `/api/scenarios` is gated by the same service rather than by a second check.
- **Session-scoped.** The compiled card goes into a `CardRegistry` overlay for that one scenario —
  never the live corpus, never a deck, never another game. Drop the source and the name stops
  resolving, which the tests pin.
- **Whole cards only.** A card any of whose lines Assay cannot read is *refused*, with the line that
  stopped it. Nothing here can produce a card missing an ability, which would look right on the
  board and test green.
- **The corpus is still hand-written.** Ground truth stays a `cardDef` with a passing scenario test.
  Nothing loads `mtg-sets` through this, and the module's own dependency is still `:mtg-sdk` alone.

Two things the compiler does that the grammar deliberately does not. It reads the **header** —
mana cost, type line, power/toughness, loyalty, defense — which is not Oracle text and which no rule
touches; a `*` power declines rather than becoming 0, because mapping a characteristic-defining
ability into the stat slot is grammar work nobody has done. And it **re-mints ability ids**: the
grammar mints one fixed constant per family (no printed word determines an id, and the differential
normalizes by position), but a played card is dispatched on those ids, so two abilities sharing one
would activate the wrong ability.

At today's fineness — the "cards fully covered" line in `just assay-report`, well under a fifth of
the corpus — pasting a *random* real card more often declines than compiles. That is the tool working: the decline names the missing capability, and it
is the same ranked backlog `assay-report` produces. A custom card written in canonical templating
inside a covered family compiles, and one that does not is usually a card to reword.

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
