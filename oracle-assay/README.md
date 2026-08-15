# `:oracle-assay` — Argentum Assay

A first-party Oracle-text parser: Scryfall JSON in, `mtg-sdk` models out, with a grammar where every
rule is written in both directions so it proves itself against the whole corpus without a human
reading the output.

Design: [`docs/oracle-assay.md`](../docs/oracle-assay.md) · Build order:
[`docs/plans/oracle-assay.md`](../docs/plans/oracle-assay.md)

**Phase 1 is implemented: the kernel, invertible normalization, the touchstone gate, and a grammar
covering vanilla cards and keyword-only abilities. The differential gate is live** over the class the
grammar reads whole. Nothing here changes `:mtgish-tooling`, which stays authoritative until a
per-set cutover replaces it (Phase 5). Assay is **not a runtime card loader** and never will be.

## Commands

```bash
just assay parse "Serra Angel"      # normalized lines + the SDK model each parses to
just assay explain "Wall of Omens"  # the same, with a caret on the token a decline died on
just assay-gate                     # the touchstone over the whole corpus; exit 1 on a bug
just assay-report --top 40          # the same numbers, always exit 0
just assay-report --scope           # restricted to Phase 1's own target class
just assay-differential             # Assay's readings vs. the hand-written cards
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
grammar/    the rules, by topic — Phase 1 is Primitives + Keywords
gate/       the touchstone and the fineness report
cli/        assay parse | explain | gate | report | corpus
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
Ability lines                    66793  (39746 unique)

Round-trips byte-exact           12646   189.3‰ (18.9%)
Alternate spelling normalized    30
Declined                         54117
Ambiguous — distinct readings    0
Print mismatch                   0
Normalization not invertible     0
Full inverse not reproduced      0

Vanilla + keyword-only cards     1439 / 1712   840.5‰ (84.1%)   <- Phase 1 target
Reminder-text glosses            2868 matched · 116 differed · 957 unglossed
```

Fineness is **parts per thousand**, per the assay the module is named for — 840.5‰ is 84.1%.

The machinery holds: **zero** ambiguities, print mismatches, or non-invertible normalizations
across 66,793 ability lines. The 84.1% on Phase 1's own target class is not the round trip
faltering — every remaining line in that class declines because the SDK has no vocabulary for the
keyword, which `just assay-report --scope` lists in rank order.

## What Phase 1 already found

The report is two documents at once, and the second one is about `mtg-sdk`:

- **Two keyword abilities of identical shape are modelled two different ways.** `Enchant creature`
  (1,289 cards) is an aura's attachment restriction and `Equip {2}` (621 cards) is a
  `CardDefinition.equipCost` field — neither is a `KeywordAbility`, so neither has anything to
  parse *into*. They are the two largest keyword-only decline families in the corpus.
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
    compared                         449
    not yet covered by the grammar   7630
    script slot not modelled yet      33
    multi-face (out of scope)        301
    Oracle text differs from golden  461
    golden would not decode            0

  Confirmed — models agree           444   988.9‰ (98.9%)
  DIVERGENT — read every one           5
```

Three separate things have to hold before a card is compared, and each has its own bucket:

| Guard | Why |
|---|---|
| Assay reads every **line** | A keyword whose line declined would look like agreement. |
| The text is the **same text** | A golden carries the wording it was authored from; if that is not what Scryfall serves, Assay is reading one card and diffing another. Compared normalized, so inconsistently-included reminder text is not a difference. |
| The definition uses only **modelled slots** | A keyword the SDK lowers to a triggered ability at authoring time leaves content the grammar cannot produce. Confirming it would claim a check nobody performed. |

A divergence never fails the build — it is a finding to classify as **parser bug**, **card bug**, or
**fold**. Only an undecodable golden exits non-zero. The fold list lives in `gate/Differential.kt`
and is reviewed rather than grown silently: every entry is a divergence the gate stops reporting, so
each one has to say why it is not a difference.

### What the gate has found

- **A parser bug of exactly the class this gate exists for.** Assay read "protection from black and
  from red" as one `Protection(Colors([BLACK, RED]))`; the cards spell it as two `Protection(Color)`
  abilities. **The cards are right** — CR 702.16g: *"'Protection from [quality A] and from [quality
  B]' … behaves as two separate protection abilities."* The reading round-trips perfectly and means
  the wrong thing, so the touchstone could never have caught it. Affects Paladin en-Vec, Sabertooth
  Nishoba and Akroma, Angel of Wrath. Fixing it means the protection rule must yield a *list*, which
  collides with `Primitives.colorScope` being deliberately one rule to avoid an ambiguity error.
- **A third pair of SDK spellings for one concept.** `KeywordAbility.Flanking` (a `data object`) and
  `Simple(Keyword.FLANKING)` both exist and are not equal; the cards use the second and the grammar
  emits the first. Same shape as the `PROTECTION_FROM_EACH_OPPONENT` finding below, and it raises a
  live question about which spelling the engine reads.
- **Two implementations of Affinity in the corpus.** Frogmite spells it `KeywordAbility.Affinity`;
  Qumulox, Memory Guardian and the five Darksteel golems hand-roll the same text as a
  `ModifySpellCost` static ability. Both work — this is an inconsistency, not a bug — but it is the
  same "one concept, two spellings" family, and it is why those cards sit in the
  `script slot not modelled yet` bucket rather than being compared.

And the gate paid for itself before its first report: writing it surfaced that "Plains"
de-pluralized to `Subtype("Plain")` — the "Elves" → `Elve` failure, live on the basic land types,
round-tripping perfectly the whole time. `Primitives.pluralSubtype` now ranks candidate readings
against the SDK's own type lists instead of guessing. Running it then surfaced the join and
slot-completeness holes above, each of which was the gate finding a way it could have lied.

## Adding a rule

1. Write it in `grammar/`, bidirectionally, through an SDK companion factory.
2. `just assay parse "<a card that uses it>"` — check the verdict, not just that it parses.
3. `just assay-gate` — the number that matters is that `MISMATCH` and `AMBIGUOUS` stay 0.
4. Add the surface form to `KeywordGrammarTest`'s round-trip list.

Two traps the kernel cannot catch for you:

- **A `match` half that quietly matches nothing** still compiles and still parses; it shows up on
  the corpus as a print mismatch far from its cause. The `every keyword rule can print what it
  parses` test exists for this.
- **Reversible but wrong.** "Elves" de-pluralizes to `Elve` and round-trips perfectly while meaning
  nothing. The touchstone structurally cannot catch that class — run `just assay-differential`, which
  is the general answer and has already caught two of these.
