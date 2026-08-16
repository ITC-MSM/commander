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

The most recent work is the **cost band** — what you pay, everywhere you pay it. It is the largest
single delivery a family has made here (**+274 whole cards**, 7,177 → 7,451) and the first one that
needed no new grammar *machinery* at all, only a refactoring: `CostAtom`'s own KDoc calls itself
"the one cost language", and the grammar now reads it that way round — one `Phrase<CostAtom>`
vocabulary lifted into an activated ability's cost and into a spell's additional cost, instead of
two vocabularies over the same English. See [the cost band](#the-cost-band) below; it also found
three hand-written cards whose noun phrases were wrong inside a cost, where nothing had ever looked.

Before it came the **spell-cast band** — "Whenever you cast a noncreature spell, …" — which
gives the grammar its first noun phrase for a *spell* rather than a permanent, and was the largest
single family left in the corpus by the honest ranking: 504 cards declined on nothing but a
spell-cast trigger, against 263 for the next one. See [the spell-cast band](#the-spell-cast-band)
below; it is also where the ranking method itself changed, from the token a line died on to the
**tail** the parse could not read. Then the **Bloomburrow band** — the first set picked *because*
its cards are already implemented, so every decline was a grammar gap with a written answer.

Before it came the **counters band**, the first one picked by *ranking the backlog* rather than by
picking a set: `just assay-report --implemented` said 656 cards with a hand-written golden decline
on nothing but a counter sentence, the largest sole-blocked family in that population. See
[the counters band](#the-counters-band) below.

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
just assay-report --rank tail       # declines keyed on the parse's tail, with the sole-blocked count
just assay-differential             # Assay's readings vs. the hand-written cards
just assay-explore                  # all of the above in a browser, on the live grammar
just assay-bake                     # re-bless the whole-card verdict ledger (see below)
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
grammar/    the rules, by topic — Primitives, Keywords, Cardinals, Conditions, Filters, Spells,
            Targets, Steps, Continuations, Triggers, Mana, Costs, Activated, Replacements, Statics,
            Restrictions, and the effect-topic files Library, Hand, Combat, Graveyard, Stack,
            SelfSteps
            Filters is the noun phrase for a permanent and Spells the one for a spell — same
            GameObjectFilter, different head noun, disjoint positions
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
Ability lines                    66793  (38843 unique)

Round-trips byte-exact           24993   374.2‰ (37.4%)
Alternate spelling normalized    1247
Declined                         40553
Ambiguous — distinct readings    0
Print mismatch                   0
Normalization not invertible     0
Full inverse not reproduced      0
Redundant readings (same model)  0

Cards fully covered              7177 / 34882   205.8‰ (20.6%)
Vanilla + keyword-only cards     1444 / 1712   843.5‰ (84.3%)   <- Phase 1 target
Portal (set POR)                 200 / 200     1000.0‰ (100%)   <- the Portal band's target
Legions (set LGN)                145 / 145     1000.0‰ (100%)   <- the Legions band's target
Bloomburrow (set BLB)            58 / 280      207.1‰ (20.7%)   <- the Bloomburrow band, in progress
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

## The equipment band

`Equip {§}` was the **largest single sentence shape in the corpus** — 563 cards, against 342 for the
next one — and the last of Phase 1's two headline findings still declining. It is now read, together
with the two attached-permanent sentences an Equipment shares with an Aura. Whole-corpus coverage
went 6,335 → **6,400 cards**, byte-exact lines 22,944 → **23,861**, and the differential's compared
population 2,431 → **2,449**, every one of the 18 new cards confirmed.

**The band is two changes in two different files, and which half went where is the whole point.**

**The noun is normalization's.** An Equipment prints "Equipped creature gets +2/+0." for a model that
is *byte-identical* to an Aura's: the static's affected set is `GroupFilter.attachedCreature()` —
`Permanent` scoped to `AttachedTo` — which says "the thing this is attached to" and nothing about
auras, so Bonesplitter's golden and Holy Strength's carry the same `ModifyStats`. Which word a card
prints is a function of its type line, exactly like the self-reference noun, and the model has
nowhere to keep it. A second grammar rule would therefore have given one value two printed forms and
left `unparse` to choose — ambiguity by construction, which is invariant 2 rather than a preference.
So `Normalizer.canonicalizeAttachmentNoun` abstracts "equipped creature" onto "enchanted creature"
and restores the printed word positionally, and *every* static rule in `Statics` reads both card
classes without knowing there are two. `Statics`' KDoc predicted this pass before it existed; the
band carried the prediction out rather than revising it.

**The keyword is a line rule, because equip is lowered rather than stored.** "Equip {1}" is
`CardDefinition.equipCost` *plus* a synthesized activated ability carrying CR 702.6a's attach effect,
sorcery timing and target requirement. That is one printed line filling two slots in two different
objects, which is `Grammar.amplifyLine`'s shape and the reason `CardFragment` grew an `equipCost`
field — a fragment is the only place a line's two contributions can meet.

**The rule does not reproduce the lowering; it calls it.** `CardBuilder.equipAbility`'s body moved to
`ActivatedAbility.equip` in the same change, and both callers use it. A second copy in `grammar/`
would have agreed with the cards exactly until someone edited one of them, and the differential would
then have reported every Equipment in the corpus over a change nobody made to a card. It is the one
place the module's "build through an SDK facade" rule needed the facade to be *created* first, since
equip's curated surface was a DSL method a parser cannot call.

**`equipCost` is the first compared field outside `CardScript`.** Comparing only the ability would
confirm an Equipment that can never be equipped: `CardValidator` requires an Equipment type line
wherever the field is set and `CardLinter` reads it to decide whether a permanent can ever attach, so
a card carrying the ability without the cost is a different — and worse — card. The differential's
header now names it beside the script slots.

**The band is also the third worked example of a decline rank overstating its work**, and this time
the overstatement was measured in advance rather than after. Of a 400-card sample blocked by
`Equip {§}`, 248 declined on *nothing but* equipment-shaped lines — which predicted ~350 whole cards.
The actual figure is 65, and the gap is one word: the sample counted "Equipped creature gets +2/+2
and has trample **and** lifelink" as an equipment shape, and the grammar's joined sentence takes one
keyword rather than a run. So the prediction was right about which cards the band reaches and wrong
about which *lines* it finishes. The residue is now visible and small: 385 cards decline on nothing
but an "Enchanted creature …" sentence, and their tail is genuinely long — the largest single one is
10 cards ("doesn't untap during its controller's untap step"), and a keyword-run generalization of
the joined form is worth 22. There is no fourth large family hiding behind this one, which is the
useful half of the finding.

## The spell-cast band

"Whenever you cast a noncreature spell, …" — the spell-cast triggers, and with them the first noun
phrase the grammar has for a **spell** rather than a permanent. Whole-corpus coverage went
6,400 → **6,583 cards**, byte-exact lines 23,861 → **24,139**, and the differential's compared
population 2,449 → **2,495**.

**It is the second band picked by ranking the backlog, and the ranking method is the part that
changed.** The counters band ranked by "cards whose line dies at the verb"; this one ranks by *what
the grammar could not read* — every declined line is re-parsed, the parse's death offset taken, and
the **tail from that offset** is what the families are keyed on. That is the one key that neither
over- nor under-counts a prefix: a line that dies at "you cast" has already read "Whenever ", so the
family it names is the missing *event* rather than the word the report's table shows. By that
measure a spell-cast event is far and away the largest family in the corpus — **504 cards decline on
nothing but a spell-cast trigger**, against 263 for the next one, and 936 touch one.

**And it was measured before it was written, which is the habit the equipment band's overstatement
bought.** Substituting a known-good prefix ("When ~ enters, ") into all 712 of those cards' declined
lines and re-parsing says how many payoffs the grammar can already read: **252 of the lines and 234
whole cards**. The band delivered 183, and the residue is nameable rather than mysterious — the
`SpellCastPredicate` riders ("from your hand", "a kicked spell", "a spell that targets ~"), the
colour disjunction ("a blue or black spell"), "a colorless spell", and "your first spell during each
opponent's turn", which is not an each-turn ordinal at all. For comparison, the same measurement run
on modal spells — the family the token table and the shape table both rank first — says **126 whole
cards**, because a modal card is finished only when *every* one of its bullets reads.

**The whole band is rows plus one noun phrase, and no SDK change at all.** `SpellCastEvent`,
`NthSpellCastEvent` and `CastThisSpellEvent` were already modelled with curated facades in front of
them, so this is the cheapest thing the module can be doing: a grammar gap whose answer was already
written, and which the differential confirms the moment it parses.

- **The noun phrase is [`Spells`](src/main/kotlin/com/wingedsheep/assay/grammar/Spells.kt), and it is
  a family rather than rows in `Filters` because of the head noun.** A permanent phrase's head is the
  card type ("creature", "nonbasic land"); a spell phrase's head is the literal word "spell" with the
  card type in front of it as an adjective. `GameObjectFilter.Creature` is therefore printed
  "creature" in one file and "creature spell" in the other — one printed form per model in two
  disjoint positions, not two forms for one. The layers are a deliberate *subset* of `Filters`',
  because a spell has no controller, is never tapped or attacking, and has no power to compare: what
  is left is the three axes a card carries on the stack, its types, its colour and its mana value.
- **The subtype join is "or" here and "and/or" there, and that is not two spellings of one model.**
  `Filters.anySubtype`'s inner is a type noun, so the value it builds always carries a card-type
  predicate under the `Or`; this one never does. Deriving the join from the head noun would be a rule
  reading a templating habit instead of a model.
- **The caster is a field on the event, so "you" / "an opponent" / "a player" are three rows over one
  skeleton** — not a subject vocabulary in a slot, which would also let the rule print "each player
  casts", a sentence no card writes.
- **The effect clause is `Steps.triggeredStep`, for the filtered-trigger reason.** The event names an
  object of its own — the spell being cast — so "it" in the payoff is that spell, the third anaphor
  position exactly as a filtered enters-trigger is. "When you cast this spell" is the one row that
  takes the *source* cascade instead, and it has to: there the spell being cast **is** the source.
- **Widening `filteredTriggerRule` is what made the family rows.** Its `article: Boolean` became a
  noun-phrase parameter, so a cast trigger passes `Spells.indefinite` through the identical rule the
  enters and becomes-blocked triggers use, and nothing about the shape was copied.

**What the gate found: 46 newly-compared cards, and 4 divergences that are four different things** —
which is roughly the ratio the differential is supposed to have, and the first time a band has
produced one of each kind.

- **A parser bug of the reversible-but-wrong class — Storyteller Pixie.** The subtype layer read
  "an **Adventure** spell" as `Any.withSubtype(Adventure)`. The card is right and the grammar was
  wrong: CR 715.3 makes an Adventure spell one *cast as* an Adventure, which is what
  `SpellCastPredicate.CastAsAdventure` says in its own KDoc — "this is about how the card was cast,
  not what the card is" — and the same adventurer card cast as its creature half does not satisfy it.
  The reading round-tripped byte-perfectly and denoted a trigger the engine would never fire, which
  is precisely what the touchstone structurally cannot see. Fixed by spelling the phrase as a row of
  its own and having `Spells.spellSubtype` refuse the word, since one printed form with two models is
  ambiguity by construction rather than a preference.
- **A card bug of the bare-tribal-noun class — Adeliz, the Cinder Wind.** "Wizards you control get
  +1/+1" filtered on `Creature.withSubtype(Wizard)`; a bare tribal noun names every *permanent* with
  the subtype, which is the reading Zombie Master proves by printing both spellings on one card. It
  is the residue of the migration that closed that finding: Adeliz was not in the compared population
  then, and is now. Unobservable today, and fixed for the same reason the other 103 were.
- **A card bug of the "you may" class — Daring Archaeologist.** "You may return target artifact card
  from your graveyard to your hand" was spelled `optional = true` on the *target requirement*, which
  is the SDK's phrasing for "up to one target" — a strictly different ability, and exactly the
  conflation the trigger-`optional` collapse removed from the engine. The consent belongs on the
  ability. Fixed with the scenario test that asserts the observable halves: the requirement's minimum
  is 1, and declining asks for no target at all.
- **The standing `ManaColorSet.Specific` finding, recurring — Spider Manifestation.** "{T}: Add {R}
  or {G}." as one `AddManaOfChoiceEffect` where 165 cards write two abilities. The README's own note
  said none of the thirteen was compared "because each one's rider declines anyway"; this band read
  the rider. Read afterwards and classified a **card bug**: the note says `Specific` earns its place
  on the riders the two-ability form cannot express, and this line has no rider — the card was in the
  wrong group. Fixed to two abilities, with a scenario test. The finding below is unchanged and still
  not folded.

**Where the ranking points next.** On the same tail ranking, the row under `you cast` at 504 was
`When ~` at 263 and then a flat run — `enchanted creature` 183, `for each` 175, `Until end of` 170,
`Each player` 165 — and none of those is one sentence the way a cast trigger is. (Those counts are
the pre-band measurement and can only have risen, since a card blocked by two families was
sole-blocked by neither.) A flat tail is the shape the equipment band's residue had, and it is the
signal that the next target is a *set* rather than a family.

## The Bloomburrow band

Bloomburrow is 280 cards, every one of them implemented by hand here, which makes it the first set
picked *because* the goldens exist: every line it declines is a grammar gap whose known-good answer
is already written, and the differential confirms each one the moment it parses. The band took the
set 42 → **58 cards** and the whole corpus 6,583 → **7,177**, and it is four pieces of machinery plus
rows — the ratio the module's "cards covered per rule" curve is watching for.

**A granted keyword is a run, not a keyword.** `Keywords.keywordRun` spells "trample",
"lifelink and indestructible" and "trample, hexproof, and indestructible" as the list the model
actually holds, and every grant position slots it: to a target, to a group, to the source, to the
enchanted permanent. It *replaced* rules rather than adding them — `SelfSteps` carried a
two-keyword rule with no singular sibling, which is what a family looks like before it is one, and
`Statics.conditionalSelfStatic` collapsed a `pairForm` boolean into a three-member enum that also
gained the keyword-only sentence ("As long as you've lost life this turn, ~ has flying and
vigilance") for free. One grant is the bare effect and several are a composite, because that is what
`CompositeEffect` means and what every hand-written card holds; a rule that printed the singular as
a one-element composite would have disagreed with all of them.

**A token's count, colours and keywords are slots.** Six rows out of two axes — the count ("a",
a number word, "X") and the keyword rider — plus a colour *run* with `keywordRun`'s shape over
`Set<Color>`. The sets have no order and the printed sentence needs one, so colours print WUBRG and
keywords print in `Keyword`'s declaration order: both are `Color`/`Keyword`'s own, and a card that
built its set the other way round still prints the sentence Oracle prints. The predefined nouns
(Food, Treasure, Clue, Blood, Map, Lander, Shard) are a second family, each row calling the facade
the SDK publishes for it — with "investigate" deliberately left out, because CR 701.36a makes it the
same model as "create a Clue token" and two canonical spellings would leave printing undecided.

**An ability word is printed shape, and belongs to normalization.** CR 207.2c: *"they have no
special rules meaning and no individual entries in the Comprehensive Rules."* So `Landfall — `,
`Threshold — ` and `Valiant — ` come off the line, are recorded per line index, and go back on in
`restore` — the alternative being a grammar rule per ability word wrapping every sentence the grammar
already reads, which is the multiplicative cost "lift, don't re-spell" exists to refuse. The list is
**CR 207.2c's, verbatim, rather than a pattern**: CR 207.2d's *flavor* words have the identical
printed shape and are unbounded, and so is a Saga's `I —` and a Class's `Level 2 —`. Worth 106 cards
corpus-wide on its own, and it is what let the differential see the landfall bug below.

Then three rows: Valiant's trigger (one `TriggerSpec` the SDK already publishes), "deals N damage to
target opponent", and Threshold's graveyard count — which turned the hand-size comparison into a
two-member shape over (player, zone, direction).

**What it found.** Fifteen bugs in hand-written cards and one in `mtg-sdk`, every one surfaced by the
differential on the day a line stopped declining:

- **`Triggers.LandYouControlEnters` was `TriggerBinding.OTHER`** — one facade, 29 cards. No landfall
  ability prints "another"; the distinction is invisible on a creature and load-bearing on a *land*
  with a landfall trigger, which under `OTHER` would silently not see itself enter.
- **Five more bare-noun-is-permanents cards** — Kargan Dragonrider, Corsair Captain, Lathliss,
  Inside Source, Voice of the Woods, all `IsCreature` where the printed noun names only a subtype.
  The same class the bare-tribal-noun migration fixed; these were never comparable before.
- **Sunstar Expansionist read its intervening "if" as a resolution-time gate** — "When this creature
  enters, **if** an opponent controls more lands than you, …" is CR 603.4, checked when the trigger
  would go on the stack *and* again on resolution, not a `Gate.WhenCondition` that always triggers.
- **Seven BLB cards printed text the card does not have** — five keyword runs in the wrong order
  (Scryfall prints "Reach, vigilance"), Hunter's Talent on the pre-Bloomburrow "enters the
  battlefield" wording, and Quaketusk Boar with no `oracleText` at all.
- **Shocking Sharpshooter restated a documented default** — `damageSource = Self` is what `null`
  already means for a permanent's own triggered ability. 72 other cards carry the same redundancy
  and will surface as the grammar widens; it is *not* folded, because the field means something
  when it is set to anything else.

**Where the ranking points next, inside this set.** Gift is the largest family left by a wide margin
— **17 BLB cards are sole-blocked by it** — and it is two halves rather than one: the `Gift a card`
keyword line and the `If the gift was promised, …` rider, the second of which needs its base clause
to read first. After it: the Class levels (`{3}{U}: Level 2`, 10 cards), modal spells (8), and
"Spend this mana only to cast …" (6).

## The cost band

What you pay, everywhere you pay it. Whole-corpus coverage went 7,177 → **7,451 cards**, byte-exact
lines 24,993 → **25,377**, the differential's compared population 2,698 → **2,747** — and, being an
`mtg-sdk`-shaped band, it needed no new SDK type at all: every atom it reads was already in
`CostAtom`, waiting for a sentence.

**It is the largest band the tail ranking has produced, and the probe agreed with it before a line
was written.** Ranked by the parse's tail, the top rows are the modal bullets and a scatter of
trigger prefixes; the cost family does not appear as a row at all, because a cost is *the text before
a colon* and the tail keys on what came after. Ranking the colon lines directly — substituting `{T}: `
for every cost the grammar could not read and re-parsing — says **465 whole cards are blocked on
nothing but the cost clause**, against 104 for the modal band and 111 for cost reduction. A second
probe, greedy over the individual atoms, ranked the rows: "Discard a card" alone finishes 86 cards,
counter removal 49, the singular tap 35, "Sacrifice another" 35. The band delivered **274**, which is
the sum of exactly the rows that were written; the residue is named below and each piece of it is a
different band.

**One vocabulary, two contexts, because the SDK says so.** `CostAtom`'s own KDoc calls itself "the
one cost language": a payable thing is declared once and each *context* carries it through its own
`Atom` wrapper. Assay had it the other way round — `Costs` read a list of cost sentences for an
activated ability, and `Restrictions.additionalCostLine` was a separate rule that read
"sacrifice {filter}" and nothing else. Two vocabularies over one English. So `Costs.atoms` is now a
`Phrase<CostAtom>`, and the two contexts are two *lifts* of it. Adding "discard a card" to the
activation side gave "As an additional cost to cast this spell, discard a card." for free, and the
test for that property is one assertion in each of the two files.

**What is deliberately *not* an atom is the other half of the argument.** "Sacrifice ~", "Exile ~",
`{T}`, `{Q}`, "Exert ~" stay `AbilityCost` cases and are unreachable from the spell side — for the
reason `CostAtom` gives for keeping `excludeSelf` off it: *a spell being cast has no source
permanent*. That is a rule rather than a gap, and `RestrictionsTest` asserts the decline.

**Capitalization stopped being an `alternate` and became a parameter.** A cost atom is the one clause
Oracle capitalizes that is not a sentence start, so while costs lived in one position the lowercase
spelling could be an `alternate` — parseable, never printed. It no longer holds: in
"As an additional cost to cast this spell, **sacrifice** a creature." the lowercase form is
*canonical*. The vocabulary is therefore a function of its leading word's spelling, instantiated
twice, which is also what stops a row existing in one capitalization only.

**Three card bugs, all of the same kind, all invisible in play.** The differential went 0 → 3 the
first time the gate could read a cost's noun phrase, and every one of them was a hand-written card:

- **Wirewood Symbiote** and **Fungal Plots** spell "an Elf you control" and "two Saprolings" as
  `GameObjectFilter.Creature.withSubtype(…)`. A bare tribal noun means any *permanent* of that
  tribe — the same correction the 103-card migration made everywhere the grammar could already see,
  and these two survived it because their nouns were inside a **cost**, which nothing read until now.
  Unobservable today, since every printed Elf and Saproling is a creature.
- **Gene Pollinator** writes `Costs.TapAnotherPermanent()` for "Tap an untapped permanent you
  control" — a text with no "another" in it, and a filter left at the facade's unqualified default
  where the noun is "permanent". The `excludeSelf` restated what the co-paid `{T}` already
  guarantees.

**Where the ranking points next, in this family.** The same greedy probe names the rows left, and
they are three different bands rather than more rows:

- **The self costs that are activated from another zone** — "Discard ~" (25 cards) and
  "Exile ~ from your graveyard" (22). Both need `ActivatedAbility.activateFromZone`, which the cost
  clause *determines* — you cannot discard a permanent, and "from your graveyard" says where the card
  is. That makes the cost rule's result a pair of (cost, zone) rather than a cost, which is a change
  to `Costs.cost`'s type and to `Activated`'s four call sites: a band of its own, not a row.
- **The keyword-labelled costs** — "Exhaust — {5}{U}{U}" (6), "Power-up — {5}{G}{G}" (8),
  "Boast — {4}{R}" (7), "Max speed — {3}" (10), "Renew —", "Forecast —", "Waterbend". Each is a flag
  on `ActivatedAbility` plus a printed prefix; one shape, seven rows.
- **Filter gaps, not cost gaps** — "Sacrifice a Food" (14) and "Sacrifice another creature or
  artifact" decline in [`Filters`](src/main/kotlin/com/wingedsheep/assay/grammar/Filters.kt), not
  here: the artifact subtypes the SDK publishes no list for, and the type disjunction.
- **`{S}`** (20 cards) is an **SDK gap** — `ManaCost` cannot express snow mana, so `Primitives.manaCost`
  declines rather than inventing a symbol. That one is `add-feature` work.

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
  `CardFragment` did not model. That is why the pair stopped being one finding — and the equipment
  band below is what closing the second half cost: a new `CardFragment` field, a shared factory in
  the SDK, and a normalization pass, against the aura band's one rule.
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
  Hand-written cards                 9131
    compared                         2698
    not yet covered by the grammar   5793
    script slot not modelled yet      88
    lines do not fold into one card   54
    multi-face (out of scope)        301
    Oracle text differs from golden  197
    golden would not decode            0

  Confirmed — models agree           2698   1000.0‰ (100.0%)
  DIVERGENT — read every one            0
```

**The count is back at zero, and the card that took it off zero was a card bug.** The Bloomburrow
band's one standing divergence was the already-open `ManaColorSet.Specific` finding, recurring on
Spider Manifestation exactly as the note below predicted it would: "{T}: Add {R} or {G}." written as
one `AddManaOfChoiceEffect` where 165 cards write two abilities. What the finding's own note says
justifies `Specific` is a *rider* the two-ability form cannot express correctly — and Spider
Manifestation's mana line has no rider at all. It was the first of the thirteen to become comparable
and the one that did not belong in the group; it is now two abilities, with the scenario test that
asserts the observable half (activating adds the colour straight into the pool, with no colour
decision to make). The finding itself stands, unchanged and still not folded: the other twelve keep
their riders, and the grammar still never emits `Specific`.

The count was at zero after the sweep and the two findings after it; the spell-cast band took it to
four and three of those were fixed in that band, which is the gate behaving the way it is supposed to
— zero is a checkpoint, not a property. The sweep itself took the count from 122 to 1: every
divergence the gate had
accumulated was read, classified as parser bug / card bug / fold, and acted on. The one it left
standing was Lavaborn Muse, closed by the CR 603.4 split below. What the sweep found, by kind:

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
  intervening-if left duplicated in the effect as well as lifted into `interveningIf`; Chromatic
  Sphere read as an instant-speed ability because the mana effect sat under a composite (CR 605.1a
  says "could add mana", not "does nothing else"); a two-pass reading of "creatures you control get
  +3/+3 **and** gain trample"; and two sentences printed in a spelling the corpus never uses.
- **One SDK finding acted on, one filed.** Acted on: `manaAbility = true` now derives
  `timing = TimingRule.ManaAbility` in `CardBuilder`, so the two spellings of one fact can no longer
  drift (24 cards carried only one, and the AI's `ExpiringGrantWindow` branches on `timing`).
- **One engine bug, and the only finding so far whose fix was in neither a card nor a rule: the
  engine never performed CR 603.4's second intervening-if check.** Beastbond Outcaster drew its card
  even when the 4-power creature was killed in response, and nine cards had hand-written a redundant
  resolution-time gate to compensate — a second condition the printed line does not spell, which is
  what made Lavaborn Muse a *divergence* here. See
  [Lavaborn Muse, and the CR 603.4 split](#lavaborn-muse-and-the-cr-6034-split).

The sweep's last pass closed three more, one per kind — a card bug, a scope bug and the third
anaphor — and each is worth reading for its shape rather than its card:

- **Two card bugs, `TriggerBinding.OTHER` for text that says "an artifact you control"** (Donatello,
  Way with Machines and Mm'menon, Uthros Exile). `OTHER` claims a self-exclusion the text does not
  make, and neither card is an artifact, so nothing observable changes *today* — which is exactly the
  reason it survived review, and exactly what "they happen to agree today is not a reason" rules out
  as a fold. The reading only becomes observable if the creature is ever made an artifact.
- **Kalastria Highborn — the gate's scope stopped at the first clause.** "You may pay {B}. If you do,
  target player loses 2 life **and** you gain 2 life." read as `Composite[Gated{LoseLife}, GainLife]`,
  i.e. you gained the life even when you declined to pay. The outer clause-sequence rule took the
  " and " join before the gate could. The fix is structural rather than an alternation reorder: the
  pay-gates are **no longer members of `simpleClause`/`laterClause`**, so nothing can be joined after
  one, and their consequence slots a clause *run* that owns the rest of the sentence. That is one
  reading, not a preferred one — and it is what Oracle templating means, as Extort's own reminder
  text shows ("you may pay {W/B}. If you do, each opponent loses 1 life **and** you gain that much
  life"). The run shape is now `Steps.clauseRun`, shared by the line and the consequence.
- **Tattered Ratter — the third anaphor, and the reversible-but-wrong class again.** "Whenever a Rat
  you control becomes blocked, **it** gets +2/+0" pumped the *Ratter*: `Primitives.self` and
  `SelfSteps.anaphoric` both built `EffectTarget.Self`, and the wrong reading round-tripped
  byte-perfectly. A blanket remap inside `filteredTriggerRule` would have broken "Whenever a creature
  dies, **~** gets +1/+1", because after parsing the two spellings are the same model — the
  distinction only exists at parse time. So the vocabulary is written once as
  `SelfSteps.retargetable` and *instantiated per position*: the source cascade reads both spellings as
  the source, and the filtered-trigger cascade reads the **name** as the source and the **pronoun** as
  `EffectTarget.TriggeringEntity`. Disjoint surfaces, disjoint models, nothing for the printer to
  choose. `Steps.Cascade` is the shape both instances share, so `Steps.step` is not duplicated — only
  the dozen combinators above the atoms are built twice, and every leaf is shared. 545 filtered-trigger
  lines in the corpus spell "it" in that position; the gate's round-trips rose by 4 and its
  alternate-spelling count fell by the same 4, which is the pronoun becoming canonical for its own
  model.

### Zombie Master, and the 103 cards behind it

The last card bug the sweep closed was the longest-standing one, and it is the clearest example of
what this gate is *for*. `Filters.bareSubtype` read a bare tribal noun ("Zombies") as a **creature**
filter. A bare creature-type noun actually names every *permanent* with the subtype — the adjectival
"Zombie creatures" is what narrows it — and Zombie Master proves the distinction is deliberate rather
than stylistic by printing both spellings on one card, with the ability its bare-noun line grants
spelled "Regenerate this **permanent**".

Flipping the one `build` took the differential from 2 divergences to **104**, which is why it had
been reverted twice before: 103 hand-written cards spelled the bare noun as a creature filter, and
for almost all of them the two select the same permanents. That is precisely why it survived review —
an error that is unobservable on the cards that carry it is invisible to everything except a
differential.

It landed as a card migration *first*, then the grammar line, with the differential as the check at
every step: 104 → 26 → 4 → 1. The residue at each stage was the interesting part, because the cards
that did not fall to the mechanical edit were the ones spelling the filter some other way — and
three of those turned out to be **gaps in the SDK's own vocabulary**, with no way to write the
bare-noun reading at all:

| Added | For the printed form |
|---|---|
| `DynamicAmounts.permanentsWithSubtype` | "the number of **Slivers** on the battlefield" |
| `Conditions.ControlPermanentOfType` | "if you control a **Rabbit**" |
| `TargetFilter.PermanentInYourGraveyard` | "target **Zombie card** from your graveyard" |

Each sits beside its creature-scoped twin, and the reference doc now carries the table that says
which to reach for. A reading the SDK could not express is the finding this module exists to
produce; that it took a card migration to surface three of them is the argument for running the
migration rather than filing the divergence.

Twelve cards needed per-occurrence care rather than a blanket edit, and Kavu Monarch is the one to
know: "Kavu **creatures** have trample" and "whenever another **Kavu** enters" are two filters in one
card, and only the second moves. A replace-all would have round-tripped byte-perfectly and been
wrong — the same class the gate exists to catch, reintroduced by the fix for it.

### Lavaborn Muse, and the CR 603.4 split

The last one to fall was the one the gate had been *waiting* on, and it is the only divergence so far
whose fix was in the engine rather than in a card or in a rule. Lavaborn Muse carried its
intervening-if twice — once as the trigger's condition and once as a `ConditionalEffect` around the
effect — because the engine checked the condition only at trigger detection, so a card that wanted CR
603.4's second check had to hand-write it. That second copy is a condition the printed line does not
spell, which is what made it a divergence rather than only a rules bug, and the grammar was right
both times: Phage the Untouchable, which carried *only* the condition, was reported for the mirror
reason.

The engine fix split the overloaded field into `interveningIf` (CR 603.4 — checked when the trigger
would fire and again on resolution) and `triggerRestriction` (CR 603.2 — checked only when it fires,
which is what "Whenever this creature attacks *while* you control a Dinosaur" means, and what Burning
Sun Cavalry and Seasoned Warrenguard have scenario tests asserting). Re-read against the printed text
rather than against the field, the corpus's 510 sites are **377 intervening-"if" / 44 "while" / 89
other trigger-time restriction**, and the Comprehensive Rules overruled the first reading five times
— Offspring, Soulbond, Suspend, Impending and Gift all print an "if" that looked like a mechanic gate
(CR 702.175a, 702.95a, 702.62a, 702.176a, 702.174b), while max speed is "*as long as*" (702.178a) and
is therefore the opposite.

With the second check in the engine, all nine compensating gates are deleted — Lavaborn Muse,
Farsight Mask, Bloodhall Priest ×2, Asylum Visitor, Heir of the Wilds, Convalescent Care, Oversold
Cemetery and Edgar Markov — and the two models agree. `Triggers.abilityFor` writes `interveningIf`
and never `triggerRestriction`, so a "while" card declines rather than printing an "if" sentence that
means something else.

**The differential was at zero here, and the spell-cast band moved it off — exactly as this
paragraph predicted.** Four divergences over 46 newly-compared cards, three of them fixed on the spot
(one parser bug, two card bugs) and the fourth — Spider Manifestation, under the standing
`ManaColorSet.Specific` finding — read afterwards and fixed as a third card bug, since that card's
mana line carries none of the riders the finding says the type earns its place on. Zero is a
checkpoint, not a destination: it means every card the grammar reads whole agrees with its golden on
the day it is measured, and the next band of rules is expected to move it off again.

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
    way CR 603.4 defines it — a condition printed between the event and the effect is checked twice.
    At the time the engine checked it only once, so a card that wanted both checks had to carry the
    condition *and* a `ConditionalEffect`, and Phage carried only the condition. The CR 603.4 split
    settled it in the grammar's favour: `interveningIf` is now both checks, the compensating gates
    are deleted, and Phage was never wrong — the engine was.
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
  "{T}: Add {B} or {G}." as two `AddManaEffect` abilities sharing a cost, and a much smaller group —
  13 when this was written, 16 goldens today — write it as one
  `AddManaOfChoiceEffect(ManaColorSet.Specific(...))`. Unlike the other entries in this list the
  split has a *reason*: every card in the smaller group carries a rider the two-ability form cannot
  express correctly — "Activate only once each turn" on two abilities permits two activations — so
  the type earns its place. The grammar emits the majority and never emits `Specific`, and none of
  the smaller group is compared today because each one's rider declines anyway.

  **The divergence it threw off is also its membership test.** Spider Manifestation was in the
  smaller group with a bare "{T}: Add {R} or {G}." and no rider on it at all — so its line parsed, so
  the card was compared, so it diverged, and the finding's own reason for the type is what says it
  belonged in the majority. It is now two abilities. Generalized: **a card in the smaller group whose
  mana line *reads* is a card in the wrong group**, because a rider is exactly what makes the line
  decline. That test costs nothing to run — it is the differential, unchanged — and it is why the
  finding is worth leaving open rather than folding.

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

Three things it shows that no CLI report does:

- **Which cards are behind a decline family**, and how many of those already have a hand-written
  golden. `assay report` ranks the families; clicking one is the backlog it names, split into the
  grammar gaps (answer already written, differential confirms it the moment it parses) and the
  possible SDK gaps.
- **All three rankings side by side.** Keying declines by the token a line *died on* answers "what is
  the grammar missing"; by the **sentence shape** — the line with numbers and mana symbols collapsed
  — answers "which whole sentence needs a rule"; and by the **parse's tail** — the text from the
  decline's own offset on, cut to three words — answers "what construct would let these lines get
  further", which is the one that decides work. They disagree sharply, and the page says why.
  `assay report --rank <token|shape|tail>` prints the same three tables.
- **Whether writing a family would actually finish its cards.** The sole-blocked count says which
  cards a band *reaches*; the probe on a family's page substitutes a known-good prefix for the
  family's own span, re-parses every declined line of every card behind it on the live grammar, and
  says how many whole cards come into coverage. Landfall reads 189 cards blocked and 104
  sole-blocked, and the probe says **48**. That gap is why every band picked without this step has
  been overstated.

The corpus sweep (~5s) runs in the background at startup, so the live parser and the rule tree are
usable before the numbers land; the differential runs on first request and is then cached, because
it decodes 8,874 goldens and most sessions never open it.

**Rule usage numbers are exact rather than indicative.** The kernel records no parse provenance — a
reading is a value, and the rule that produced it is gone by the time the gate sees it. But the
*print* side is deterministic: `oneOf` prints through the first canonical alternative that can
express the value, so "which rule printed this" has one answer, and it is the same walk the round
trip depends on. A rule showing no usage printed nothing in 34,882 cards.

### The explorer inside the app

`game-server` mounts the same page and the same handlers under `/api/assay/explorer`, so the web
client's **Set Completion** view offers it as a tab beside the coverage grid. That is one `<iframe>`
and no second implementation: `explore/ExploreApi.kt` holds every route's behaviour and both servers
— `assay explore`'s loopback [`ExploreServer`] and the Spring controller — are reduced to moving
bytes. A React port of these views would have been free to drift from the gates it displays, which is
the thing "a view, never a second source of truth" exists to prevent.

Unlike the custom-card sandbox next door, the tab is **not** gated. It is a read over public card
text — no state a request can mutate, no game, no account, no corpus write — so the thing to weigh is
resources, not exposure, and the sweep is already lazy: `ExploreApi` is built and its sweep started
on the *first request*, so a server nobody opens it on fetches nothing. Where `~/.cache/scryfall`
does not exist, that first sweep downloads the bulk; where it cannot, the page stays up with its
live parser and rule tree and reports the failure. The differential needs `mtg-sets` test resources
and so answers "no goldens found" off a bootJar — one page degraded, the rest intact.

## The verdict ledger

`just assay-bake` writes `game-server/src/main/resources/coverage/assay-verdicts.json`: one sorted
line per card, saying whether [`CardCompiler`] reads it **whole** and, if not, the decline that
stopped it and the printed line that decline points at. 6,979 of 34,882 cards, at the time of
writing.

It has two readers, and the second one is why the format is what it is.

**The Set Completion view** joins it per card, which turns the *missing* half of that page into a
ranked backlog: a card nobody has authored that Assay already reads end to end needs no new grammar
and no new SDK vocabulary, so it is the cheapest work on the board. The page badges those cards,
filters to them, counts them per set, and can sort every set by how many it has. Baked rather than
computed because the production server has no Scryfall cache — the same reason, and the same answer,
as the coverage denominator in `scripts/gen-set-totals`.

**`git diff`** reads it as the regression check this module has been missing. At corpus size a change
can move thousands of verdicts and "round-trips went up" hides the twelve cards that went *down*.
One card per line, sorted by name, means a re-bake's diff *is* the list of cards whose reading
changed — so re-bless it deliberately, in its own commit, the way the card goldens are. It is
therefore not wired into the build: a stale ledger degrades into an out-of-date badge, while an
auto-regenerated one would erase the only signal that made it worth committing.

It answers with [`CardCompiler`] rather than with line verdicts because "could be implemented using
Assay" is that object's exact question. A card whose every line round-trips can still fail on a `*`
power, a second face, or `CardValidator`, and a badge reading "ready" for a card that cannot be
produced would be worse than no badge.

> Baking it for the first time found a bug the gates could not see: `CardCompiler` **threw** on a
> card with a negative printed power (`CreatureStats` requires a non-negative base and enforces it
> with `require`), so Spinal Parasite and the Un-set creatures crashed the compiler instead of
> declining. Nothing had previously handed it all 34,882 cards; the Scenario Builder's paste box
> would have answered a 500. Negative P/T is now a `HEADER` decline naming the value — an SDK finding
> reported the way every other one is — and constructing the definition is guarded, so any *other*
> model invariant becomes an `INVALID_CARD` decline rather than an exception out of a bulk run.

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
