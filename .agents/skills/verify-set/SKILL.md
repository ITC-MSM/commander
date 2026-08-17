---
name: verify-set
description: Prove a Magic set is actually finished — card-for-card complete, field-for-field faithful to Scryfall, and behaviourally sound — then archive its backlog. Builds the set's Scryfall dump and `<Set>CardFieldVerificationTest`, fixes what they surface, and writes the completion report. Use when asked to "verify set X is done", "field-verify a set against Scryfall", "is set X really complete", or when a set's backlog hits N/N and needs closing out.
argument-hint: <SET name or code>
---

# Verify a set is finished

`cards.md` reading `286 / 286` is a claim, not a proof. It says every name has *a* `CardDefinition` — not
that the definitions carry the right power, the right color identity, the right oracle text, or that the
cards work when played. This skill closes that gap and then archives the backlog.


## The four claims

Finished means all four hold. Do them in order — each is cheap relative to the next, and a failure early
saves the later work.

| # | Claim | Proven by | Stage |
|---|---|---|---|
| 1 | Every card that should exist, exists | `scripts/card-status --set <CODE>`, `just check-backlog*` | [1](#stage-1--completeness) |
| 2 | Every compiled `CardDefinition` matches Scryfall field for field | `<Set>CardFieldVerificationTest` | [2](#stage-2--field-fidelity) |
| 3 | The cards behave as printed when played | scenario tests + a self-play pass | [3](#stage-3--behaviour) |
| 4 | The repo says so | snapshot re-bless, backlog archived, report written | [4](#stage-4--land-it) |

Claim 2 is the one this skill exists for, and the one no other gate in the repo covers.
`CardDefinitionSnapshotTest` pins what we compiled against *what we compiled last time*; it is blind to
Scryfall. `CardLintTest` checks internal hygiene. Nothing else compares a card to the printed card.

## Stage 0 — the dump

Everything downstream reads one committed artifact: a full Scryfall dump of the set.

```bash
CODE=msh                     # lowercase set code
SLUG=marvel-super-heroes     # the backlog/sets/<slug> directory
curl -s "https://api.scryfall.com/cards/search?order=set&unique=prints&q=e%3A$CODE" \
  -H 'User-Agent: ArgentumEngine/1.0 (card data verification)' > /tmp/p1.json
# follow `next_page` until it's absent, concatenating `data` into one {"data":[...]} object
```

- **`unique=prints`, not `unique=cards`.** One card can have several printings inside a single set
  (showcase, borderless, Beginner Box); the per-card row Scryfall serves is an arbitrary one of them.
  Reprint rows and variant art are matched by collector number, which only `unique=prints` gives you.
- **Do not commit** This changes, so this is a snapshot for this test.
- **Do not reuse `~/.cache/scryfall/<code>.json`.** `scripts/card-status` writes it, but it keeps only
  names plus a flattened per-card subset — no `type_line`, no `mana_cost`, no P/T, no per-face objects. It
  cannot support Stage 2.

`scripts/card-status` also decides *which* cards are in scope: it partitions on Scryfall `booster` into
draft cards and extras, and drops `coverage/card-exclusions.json` entries (ante, subgames, dexterity) out
of the denominator. Take its numbers as the set's definition of done, not the raw dump size.

## Stage 1 — completeness

```bash
scripts/card-status --set <SET code> --list        # implemented vs missing, missing names listed
just check-backlog                          # cards.md headers match actual [x] counts
just check-backlog-implementations           # every [ ] is genuinely unimplemented
```

Then the two lists `card-status` does *not* diff, both of which a modern set has and none of the five
existing harnesses ever checked:

```bash
grep -n 'printings\|basicLands' mtg-sets/*/src/main/kotlin/**/definitions/$CODE/*Set.kt
```

- **`MtgSet.printings`** — reprint and variant rows. These are `Printing` vals, not `CardDefinition`s, so
  they are absent from `set.cards` and invisible to every gate in Stage 2 unless you add the reprint block
  from the template. Their `collectorNumber`, `artist`, `imageUri`, `rarity` are per-printing data that
  can be wrong independently of the canonical card.
- **`MtgSet.basicLands`** — also outside `set.cards`. `BasicLandArtOrderTest` covers art ordering;
  presence and collector numbers are yours to check.

If the set is genuinely short of cards, stop — that's `add-card` / `set-loop` work, not verification.
Report the gap and don't proceed to Stage 2 on a partial set.

## Stage 2 — field fidelity

**Read [`field-verification.md`](field-verification.md)** — it holds the test template, the loader, the
DFC face-pairing, and the taxonomy of what the five prior runs actually found. Do not write this test from
memory; the false-positive handling is the hard part and it's all in there.

The shape:

1. Add `mtg-sets/src/test/kotlin/com/wingedsheep/mtg/sets/<Xxx>CardFieldVerificationTest.kt` from the
   template, with the set code, dump path and class name substituted. 
2. Run it. It collects *every* discrepancy and asserts the list is empty, so one run gives you the whole
   worklist rather than the first failure.
3. Triage each line into **fix the card**, **fix the SDK**, or **normalize the comparison** — the taxonomy
   table tells you which, and it matters: three of the eleven classes are the harness being wrong, not the
   card.
4. Fix, re-run to zero, re-bless the snapshot (Stage 4).
5. Remove the `CardFieldVerificationTest.kt`. It's just part of this test. Should not be commited.

Field discrepancies are their own commit, separate from the harness that found them — that's what
`tla-verify` and `tmt-field-verification` both did, and it keeps the card diff reviewable without the
40k-line dump in the way.

## Stage 3 — behaviour

Fields being right does not make a card work.

1. **Important cards must have scenario test.** , all special cards and
   cards with tricky behaviour. `<CardName>ScenarioTest.kt` — `AGENTS.md`'s 
2. **Play the set.** Follow [`docs/gym-self-play-testing.md`](../../../docs/gym-self-play-testing.md):
   `just gym-server`, then build an Explicit deck that concentrates the set's cards and drive the step
   loop for both seats. This is what surfaces the class of bug a scenario test can't — a card that
   produces no legal action, a trigger that never fires, a state that can't legally exist.
3. **Route what you find.** A card-shaped fix is `add-card` territory. A missing engine capability is
   `add-feature`.


## Stage 4 - DSL


## Stage 5 - Tokens


## Stage 6 — land it

```bash
just rebless-cards                       # Stage 2's card fixes moved the golden — expected
git diff mtg-sets/src/test/resources/snapshots/cards/<CODE>.json
```

Only your set's cards should have moved. **An unrelated card in the diff means you changed shared SDK
behaviour** — stop and investigate (`verify` skill, "Expected: a card-snapshot diff").

Gate by what the diff reaches, per the [`verify`](../verify/SKILL.md) skill's table — card-only fixes plus
a new `mtg-sets` test is `just test`; anything that touched `rules-engine` is `just test-rules`.

Then close the backlog out:

Read and update all files in backlogs/sets/<slug>

Ensure that the Set file is no longer incomplete. 
`com.wingedsheep.mtg.sets.definitions.<setcode>.<SetName>Set.kt` should not have this: `override val incomplete = true`


```bash
git mv backlog/sets/<slug> backlog/archived/sets/<slug>      # dump included
# cards.md header: "**Implemented:** N / N" + a Status line saying the set is complete
just check-backlog
```

Keep `<slug>-engine-gaps.md` alongside it if the set left anything genuinely infeasible — TLA's archive
does. An "engine gap" is a documented decision, not an excuse: say what's missing and why.

## The report

A set is verified when you can state all four claims with the evidence attached. Write it into the PR body:

```
Set:              MSH — Marvel Super Heroes
Completeness:     276/276 draft cards, 0 missing, N reprint rows, 20 basic lands  (scripts/card-status)
Field fidelity:   276 cards × 13 fields vs Scryfall — 0 discrepancies  (MshCardFieldVerificationTest)
                  <N> fixed on the way: <one line per class from the taxonomy>
Behaviour:        <N> scenario tests, <M> cards without one (<reason>); self-play: <what you played, what broke>
Waived:           <card, field, why, and where the follow-up is tracked>  — or "none"
Gate:             just test — green  (does not cover: <what it doesn't>)
```

**State what the gate does not cover.** "0 discrepancies" means: against the dump, on those thirteen
fields, for the cards in `set.cards`. It says nothing about rulings, legalities, or whether the card plays
correctly. Claiming more than that is worse than claiming nothing.

## Traps

- **Zero discrepancies on a modern set is a result to distrust before you trust it.** ARN legitimately came
  back clean (78 cards, all vanilla-ish, hand-checked). A 280-card set coming back clean on the first run
  more often means the harness matched nothing — check the reported card count in the test's own output
  against `card-status` before believing it.
- **The waiver set is not a way to reach green.** `WAIVED` is keyed to an exact (card, field) pair so a
  *new* discrepancy still fails. Every entry needs a reason and a tracked follow-up in the report. A
  waiver on `power` is almost certainly a real bug wearing a disguise.
- **`checkFace` on a single-faced card is the whole object.** Don't special-case; the template's `faces()`
  returns `listOf(this)` and the same code path covers both.
- **Don't fix a card to match a false positive.** Image cache-busters, `*+N` P/T render order, and the
  back-face mana cost are the harness's problem. See the taxonomy.
- **Verification is not a licence to touch other people's work.** A failure in a card or module outside
  your set is another agent's in-flight change — report and stop (`AGENTS.md` → Hard rules).
