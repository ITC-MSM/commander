# `:oracle-assay` — Argentum Assay

Guidance for agents working in this module. Read [`README.md`](README.md) first — it is the
authoritative reference for the commands, the verdict table, the differential gate's buckets, and
what the gates have found so far. The design is [`../docs/oracle-assay.md`](../docs/oracle-assay.md);
the build order is [`../docs/plans/oracle-assay.md`](../docs/plans/oracle-assay.md).

This file is the *architecture*: the rules that decide whether a change belongs here and what shape
it has to take.

## What this module is

A bidirectional Oracle-text grammar: Scryfall JSON in, `mtg-sdk` models out, every rule written in
both directions so the corpus proves it without a human reading the output.

It is an **auditor before it is a generator**. Its output today is two documents — parser coverage,
and a continuously-updated `mtg-sdk` gap report ranked by cards blocked. It is **not a runtime card
loader** and never will be; ground truth stays a human-authored `cardDef` with a passing scenario
test.

`:mtg-sdk` is the **only** production dependency, and that is load-bearing rather than tidy. Not
`:rules-engine`, not `:mtg-sets`, not `:mtgish-tooling` — a dependency on the engine invites a
runtime loader, and one on the incumbent pipeline re-imports the vocabulary Assay exists to replace.
The differential's goldens are read as *files* (`mtg-sets/src/test/resources/snapshots/cards/`,
decoded by the SDK's own `CardLoader`), which is why the rule still holds.

## The four invariants

Everything below is a consequence of these. A change that trades one of them away is not a change to
review, it is a change to decline.

1. **Bidirectional or it doesn't ship.** A canonical rule registers `build` *and* `match`;
   `PhraseBuilder.finish` refuses to construct one without both. The gate is
   `print(parse(normalize(t))) == normalize(t)`, and a one-directional rule has nothing to prove.
2. **One printed form per model.** Where English spells one meaning two ways, exactly one rule is
   canonical and the other is `alternate(...)`. Printing must be determined by the model, never by
   `oneOf` ordering.
3. **Declining is success.** Unparseable text is counted, ranked, and named — never approximated.
   A decline is the module's *product*: it names a missing capability in Argentum's own vocabulary.
4. **A phrase never throws.** A leaf that reads a malformed symbol returns no parse. A grammar that
   crashes on the corpus cannot report fineness, and "declining is success" only holds if declining
   is always reachable.

## Mapping to the SDK

- **There is no Assay IR, and none may be introduced.** Rules parse straight into `mtg-sdk` types.
  `CardFragment` is the one near-miss and it is not a counterexample: it holds SDK values and says
  *which of a card's two behavioural slots* a line fills. Nothing in it is ever translated, only
  destructured. If you find yourself defining a type to hold "what the text means", stop — that type
  belongs in `mtg-sdk` and it is `add-feature` work, not Assay work.
- **`build` goes through the SDK's companion facades** — `Effects.Destroy(...)`,
  `KeywordAbility.flashback(...)`, `Triggers.EntersBattlefield` — for the reason cards do: the
  facades are the curated surface, and this is the half that would otherwise drift from how cards are
  actually written. `match` necessarily destructures concrete classes; that asymmetry is inherent to
  a bidirectional rule, which is exactly why the `build` half must not compound it.
- **An SDK gap is reported, never routed around.** If the SDK cannot express a card, the rule
  declines and the report ranks it. Do not model it in Assay, do not approximate it with the nearest
  effect, and do not add a type to `mtg-sdk` from inside this module — that goes through
  `add-feature`, with the SDK's own bar and its own reference-doc update.
- **Two SDK spellings of one thing get one rule, and a finding.** Registering both is genuine
  ambiguity. `Primitives.protectionScope` deliberately never emits
  `Simple(PROTECTION_FROM_EACH_OPPONENT)`; `ProtectionScope.Colors` is a scope the grammar never
  produces. Each such omission carries a KDoc paragraph naming it as an SDK finding.

## Generalizing: fewer mappings, not more

The failure mode this module was built to avoid is mtgish's — 382 bespoke variants reached one
locally-reasonable two-line change at a time. The same curve is available here in the form of one
rule per printed phrase. Five habits keep off it.

**Write the rule *shape*, not the rule.** A family is a private function returning a `Phrase`, and
the members are rows in a list: `Keywords.costKeyword`, `numericKeyword`, `simple`,
`Steps.targetedPermanentStep`, `Filters.controlledBy`, `Keywords.qualityRun`. Seventeen numeric
keywords and twenty-odd cost keywords are two shapes, not thirty-seven rules. Don't pre-abstract —
write it inline the first time, and factor when the *second* member of the shape appears.

**Lift, don't re-spell.** `Triggers` slots `Steps.step` whole and lifts its `CardScript` onto the
ability, so every step rule enriches every trigger rule for free. Any new sentence context —
activated abilities, modal spells, "you may", delayed triggers — must slot the existing effect
grammar rather than restating the verbs. The win is multiplicative; restating is additive and rots.

**Layer over a predicate bag; never compose.** A `GameObjectFilter` is a bag with no canonical
spelling, so two rules that can each print *part* of one value leave printing underdetermined. The
answer in `Filters` is layering: one alternation spells the whole type phrase, exactly one optional
suffix owns `controllerPredicate` and strips precisely that field before delegating. Every new
dimension (power/toughness, colour, subtype, tapped-ness) adds one layer that owns one field — not a
combinator that can also print the others.

**Generalize the axis when the rules define one.** `qualityRun` started as a colour-join fix and
generalized in the same change to any quality, to the Oxford-comma three-way, and to hexproof under
CR 702.11f, because the Comprehensive Rules define the join over *qualities*. Reach for the rule the
CR states, not the instance the card in front of you shows.

**Enumerate only where English carries a distinction the model doesn't — and say so in the KDoc.**
There are honest cases: "artifact or enchantment" is an ordered `Or` while "artifact creature" is two
predicates, and nothing in the shape says which; `{type}cycling` would need a lookahead that breaks
`token`'s ability to verify its own output. Both are enumerated with the reason recorded. An
enumeration with no such paragraph is a rule that has not been thought through.

**A new set is not a code event.** The corpus is all of Scryfall; `--set` is a report filter, nothing
more. Never write a set-scoped rule. If a new set forces a new *file*, that is the signal to ask
which existing family the mechanic is a member of — the expected cost of a set is rows in existing
lists, and the exceptions should be nameable.

## Fail-closed matching — the rule that catches the dangerous bug class

**A `match` half reconstructs what `build` would have produced and compares the whole model.** Not a
walk over the fields it cares about. See `Steps.targetedPermanentStep`, `Triggers.triggerRule`,
`Targets.permanentFilter`: each rebuilds and tests equality, so a script carrying an
intervening-if, an `elseEffect`, an `excludeSelf`, a non-battlefield zone or a once-per-turn cap
*refuses to print* rather than printing a sentence that quietly drops it.

A matcher that inspects only part of a value round-trips byte-perfectly while meaning something
else. That is the **reversible-but-wrong** class, the touchstone structurally cannot see it, and it
is why the differential gate exists. Equality-against-reconstruction makes the check exhaustive by
construction instead of by a list of fields someone has to remember.

The same discipline covers values the text does not determine — target slot names, `AbilityId`s.
Mint one fixed constant (`Targets.SLOT`, `Triggers.ID`); the differential normalizes both sides by
position. A rule that tried to reproduce a generated id would be reading a counter, not a card.

## Printed-shape information belongs to normalization

Line grouping, the `;` separator, reminder text, and which noun a card uses for itself are properties
of the *printed line*. The model has nowhere to put them and must not grow somewhere.
`normalize/Normalizer.kt` owns them, every pass is invertible by construction (it records what it
removed and `restore` replays the inverses), and `NormalizedFace.restore(lines) == raw` is itself
gated — a normalization that cannot round-trip its own output would let any grammar look correct.

Corollary: **never "fix" a `VARIANT` by adding a field to the model.** A variant already says the
right thing — the reading survived, only the spelling moved. Encoding the spelling would be a lie
about where the information lives.

## Ambiguity is a factoring signal

`AMBIGUOUS` is never resolved by picking a reading, reordering an alternation, or narrowing a rule
until the collision hides. Two rules that read one text into two models are a bad factoring, and the
fixes are structural:

- **Disjoint domains** — `drawOne` builds 1, `Cardinals.word` starts at 2. One printed form per
  model, nothing for the printer to choose.
- **`min` on a run** — a one-element list has no separator in it, so `separated(..., min = 2)` is
  what stops every single keyword reporting as grammar redundancy.
- **`alternate(...)`** — when both forms are real English and one is canonical.

Parsing returns *every* reading on purpose, so `oneOf` order is irrelevant and ambiguity has a
definition rather than a feeling. Keep it that way: an alternation whose order matters is a bug that
has not surfaced yet.

## Kernel mechanics worth knowing before you touch `syntax/`

| Thing | Why it is like that |
|---|---|
| `parseAt` memoizes, `parseHere` must not | Memo is keyed on (rule id, offset); that is what makes an all-readings parser affordable. |
| `ParseContext.parseCap` (64) | A span with more readings is a left-factoring bug, not genuine ambiguity, and is treated as a decline. |
| Left recursion is *reported* | It becomes a named decline, never a stack overflow — a grammar bug must not crash a corpus run. |
| `token` re-reads what it writes | The kernel cannot cross-check a leaf's two halves the way a template can, so `unparse` is verified against `read` on every call. |
| `furthest`/`expected` | The entire source of `assay explain`'s caret. Any new combinator must call `ctx.fail(pos, name)` where it gives up. |
| Declaration order inside an `object` | Initializers run in order; a rule referencing a later one reads a null out of a half-initialized object. Declare leaves first. |

## Adding a rule

1. Write it in `grammar/`, bidirectionally, through an SDK companion factory. Prefer a row in an
   existing family; if there is no family and this is the second of its shape, make one.
2. `just assay parse "<a card that uses it>"` — read the *verdict*, not just that it parsed.
3. `just assay-gate` — `MISMATCH` and `AMBIGUOUS` must stay **0**. Declines may go up or down freely.
4. `just assay-differential` — the only gate that catches reversible-but-wrong. A rise in
   `DIVERGENT` is the gate earning its keep, not a regression.
5. Add the surface form to the round-trip list in the matching test under `src/test/`.
6. If the rule reaches a `CardScript` slot the grammar could not previously produce, widen
   `CardFragment.merge` **and** the differential's completeness check — `MODELLED_SLOTS_NOTE` is the
   pointer between them, and the compiler will not remind you.

Three traps the kernel cannot catch for you:

- **A `match` half that quietly matches nothing** compiles, parses, and surfaces as a print mismatch
  far from its cause. The `every keyword rule can print what it parses` test exists for this; keep an
  equivalent meta-test for every new rule family.
- **Reversible but wrong.** "Elves" de-pluralizes to `Elve` and round-trips forever. Run the
  differential; it has already caught three of these.
- **A rule that prints a value it did not fully inspect.** See fail-closed matching above.

## Triaging the gates

- `MISMATCH`, `AMBIGUOUS`, and a non-invertible normalization pass fail the build. There is no
  acceptable non-zero value and no allowlist.
- A **divergence is classified**, as parser bug / card bug / fold — never left unexplained and never
  silenced to make a number look better. A divergence that turns out to be a bug in a hand-written
  card is the outcome worth the most.
- **The fold list (`Folds` in `gate/Differential.kt`) is reviewed, not grown.** Every entry stops the
  gate reporting something, so every entry has to state why it is not a difference — ideally in the
  SDK's own words. "They happen to agree today" is not a reason; two parallel implementations
  agreeing by accident is precisely what the gate should keep watching.
- **Scoping stays fail-closed.** A card is compared only where Assay reads *every* line, the text is
  the same text, the definition uses only modelled slots, and the lines fold into one card. Anything
  else lands in a named population bucket so the denominator stays visible. Widening a guard to raise
  the compared count is the gate lying to itself — it has happened three times, and each time it was
  found by running, not by reading.

## Scaling to the whole corpus

Phase 1 covers 5.5% of 34,882 cards with ~150 rules. The rules below are what keep the last 90%
from arriving as 3,000 one-offs. Everything here is cheap to watch now and expensive to discover
late.

**Watch three curves, as numbers rather than as principles.**

- **Cards covered per rule.** mtgish reached 382 bespoke variants one locally-reasonable two-line
  change at a time, and no single change looked wrong. The defense is a tracked number, not good
  intentions: a family whose marginal leverage collapses is N one-offs wearing a factory's clothes.
- **Redundant readings.** Two rules producing the *same* model for one text are reported and not
  gated — and they are exactly the configuration that becomes a hard `AMBIGUOUS` the moment either
  rule's model shifts. It is ambiguity's leading indicator, and the one thing here that grows
  quadratically with rule count. Noise at 150 rules; the number to watch at 1,500, ranked by which
  rules overlap.
- **The unchecked surface.** The fold list, the differential's population buckets, `alternate` rules,
  every `canonical = false`. Each is justified individually and none is free. A growing unchecked
  surface is how a green gate stops meaning anything.

**100% is the destination; coverage is the lagging indicator of it.** Fineness at 1000‰ means
`mtg-sdk` can express every card in Magic — the engine's actual goal, with Assay as the instrument
that measures it — so take the number literally. What it must never become is the quantity optimized
*directly*: coverage bought one rule per card is coverage that arrives as 3,000 rules nobody can
change, and it is reached by a sequence of individually reasonable commits. Work the ranked decline
list top-down by cards blocked and let the percentage be the consequence. 2,250 decline families is a
statement about sequencing, not a ceiling; a rule that unlocks one card and joins no family needs a
stated reason.

**Split the decline list by what is already implemented.** The ~8,900 hand-written cards are each
proof that the SDK can express that card. So a declined line on a card that already has a golden is a
**grammar** gap whose known-good answer is sitting in the goldens — the cheapest work in the module,
and confirmable by the differential the moment it parses. A declined line on a card nobody has
implemented may be an **SDK** gap, which is `add-feature` work with a much longer lead time. Ranking
those two populations separately turns one long list into two backlogs that different people can work
in parallel, and it is the fastest route to the 100%.

**Keep the gate fast, because a gate nobody runs before pushing catches nothing.** A whole-corpus run
is ~5s at Phase 1 size. Three levers when it stops being, in order, none of which changes any rule's
semantics: index `oneOf` alternatives by their leading literal rather than trying all of them (and
the print side by the model's concrete type — `unparse` walks alternatives linearly today); cache
parses by normalized line across cards (66,793 lines, 39,746 unique, and the ratio improves as the
corpus grows); run cards in parallel, which is free because the parser is pure and `ParseContext` is
per-parse. Track the rate in the report so a regression is visible.

**Regressions need a diff, not a total.** At this size a change can move thousands of verdicts, and
"round-trips went up" hides the twelve cards that went *down*. Not built yet; when the totals stop
being reviewable, the shape that fits this repo is a committed ledger — one sorted line per card,
verdict plus decline token, pinned to the Scryfall bulk's version — re-blessed deliberately like the
card goldens, so every PR shows exactly which cards changed reading and a corpus refresh is its own
commit.

**Structure that survives 40 grammar files:** one file per semantic family, never per set or card;
dependencies a DAG through each family's public entry `val` (`Steps` → `Filters`/`Targets`/
`Cardinals`, `Triggers` → `Steps`); `Grammar.kt` the only place families are combined. Rule names
stay unique and name their family, so an ambiguity diagnostic can identify both sides. Every family
gets the `every rule can print what it parses` meta-test through a shared helper, and a family that
is written but never wired into `Grammar` should fail a test rather than sit as dead code.

## Custom cards

Custom cards have no Scryfall text, so the touchstone's direction does not apply to them — but the
**inverse gate does**, and it is the more useful one: print the `CardDefinition`, reparse, compare
models. `model → text → model`. That is the Phase 4 renderer turned into a linter, and it runs on
custom cards with no new infrastructure because they are already hand-written definitions in the
goldens.

Three consequences worth getting right before the first custom set:

- **The grammar is the templating standard, and the custom card yields to it.** This inverts the rule
  for real cards. A real card that declines is an SDK gap to fix; a custom card that declines is
  usually a card to reword — its text is *authorable*, and text outside canonical Magic templating is
  text that will confuse players. Extending the grammar for a custom card is the exception and needs
  the same family test every other rule gets.
- **One grammar, never a fork.** A custom keyword is a rule like any other, in the same files, checked
  by the same global ambiguity gate. A custom surface form colliding with real Oracle text is
  precisely the finding you want; a separate custom grammar would hide it.
- **Separate the populations in the report.** A decline on a real card means "extend the SDK"; a
  decline on a custom card means "retemplate the card". Mixing them corrupts the ranked SDK backlog,
  which is this module's primary product.

The payoff is that Assay becomes a design-time tool as well as an audit: *is this card expressible in
canonical Magic templating, and what exactly does it say?* — answered mechanically, with generated
Oracle text as the by-product.

## Style

The KDoc in this module is unusually dense on purpose: **every non-obvious rule states why it is not
simpler.** That is the convention, not decoration — the alternative is a future agent "cleaning up" a
deliberate asymmetry (the singular/plural split, the enumerated type list, the omitted
`EachOpponent` spelling) and reintroducing an ambiguity the corpus took a full run to surface. If you
cannot write that paragraph, the rule is probably wrong.

Otherwise: pure functions, immutable data, no reflection, no mutable global state, `object` per
topic, rules as `val`s. Nothing in `grammar/` should need a comment explaining *what* it does.

## Commands

Run through `just`, never raw `./gradlew` — the recipes hold the machine-global build semaphore.

```bash
just assay parse "Serra Angel"      # normalized lines + the SDK model each parses to
just assay explain "Wall of Omens"  # the same, with a caret on the token a decline died on
just assay-gate                     # touchstone over the corpus; exit 1 on a bug
just assay-gate --limit 2000        # fast smoke run while iterating
just assay-report --top 40          # the same numbers, always exit 0 — the SDK gap report
just assay-differential             # Assay's readings vs. the hand-written cards
```

Unit tests are Kotest string-spec under `src/test/kotlin/com/wingedsheep/assay/`, named for the
property they assert rather than the method they call.
