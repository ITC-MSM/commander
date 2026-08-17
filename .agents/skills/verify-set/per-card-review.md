# Reviewing every card, without drowning

Stages 3–4 of [`SKILL.md`](SKILL.md): the DSL fidelity pass and the token pass. Both are *per card*, and
a modern set is 200–290 of them. This file is the fan-out protocol and the two checklists.

## Why this can't be one agent's job

Stage 2's field check is a string compare — one test run, one list of failures, no reading required.
Stages 3 and 4 are judgement: *does this `cardDef { }` do what the oracle text says?* That means reading
the card file, the oracle text, and often an SDK primitive's KDoc. Call it 200–600 lines of context per
card. At 280 cards, read serially in one session, the useful signal is long gone by card 40 and the
session is compacting by card 80 — and a compacted reviewer silently gets worse without saying so.

So the driving session **never reads a card file**. It dispatches, aggregates verdict blocks, and keeps a
ledger. This is the same doctrine `set-loop` runs on — see
[How the driving session stays flat](../../../docs/agent-loops/set-implementation-loop.md#how-the-driving-session-stays-flat)
— with one difference: verification is read-only, so batches can run wide and no worktree is needed
unless you start fixing.

**The test of whether you're doing it right:** a 280-card set must cost your context roughly what a
20-card set costs. If you're about to open a `.kt` file to "just check one thing", that's a subagent's job.

## Dispatch shape

```
enumerate cards  →  batch into units of 8–12  →  one reviewer subagent per batch  →  aggregate
                                                 (several in flight at once)
```

- **Batch size 8–12.** One card per agent wastes a whole context on a vanilla creature and floods you with
  verdict blocks; 30 puts the agent in the same drowning position as the driving session. 8–12 fits
  comfortably and keeps a red batch small enough to re-dispatch.
- **Group by mechanic or cycle, not alphabetically.** A reviewer holding all five cards of a cycle catches
  the one that renders the shared ability differently — the highest-value finding in the pass, and
  invisible to an alphabetical split.
- **Run several batches concurrently.** They're read-only and independent, so dispatch them in one message.
- **Fixes are a separate dispatch.** A reviewer that also edits will rationalise its own findings. Collect
  every verdict first, triage yourself, then dispatch fixers against the specific findings.
- **Enumerate before you dispatch**, so the batch list is real:
  ```bash
  ls mtg-sets/*/src/main/kotlin/com/wingedsheep/mtg/sets/definitions/$CODE/cards/*.kt | wc -l
  just where $CODE          # the era module and its tests/ child
  ```

Keep a ledger — a scratch file, one line per batch (`[ ] pending · [~] running · [x] verdict recorded`).
It is your memory across a pass this long; the conversation is not.

## The DSL reviewer prompt

Substitute the batch. The **Return only** block is what keeps your context flat — insist on it.

```
Review the DSL fidelity of these <SET> cards. Read-only: change no files, run no builds.

Cards: <CardA.kt; CardB.kt; …>   (mtg-sets/<era>/src/main/kotlin/.../definitions/<code>/cards/)

For each card:
1. Read the card file and its `oracleText`.
2. Read the scenario test if one exists (<era>/tests/.../<CardName>ScenarioTest.kt).
3. Answer: does the script do what the oracle text says — every clause, in the right order,
   with the right timing, targets, durations and controller? Check especially:
   - a clause in the oracle text with nothing in the script that implements it
   - a script doing something the oracle text doesn't say
   - "target" vs "each" vs "any number of"; "you may" vs mandatory
   - triggers: the right event, the right zone, intervening-if vs on-resolution condition
   - durations: end of turn vs end of combat vs permanent; "until your next turn"
   - last-known-information reads on dies/leaves triggers
4. Check it composes existing vocabulary: `Effects.*` / `Patterns.*` facades, no raw
   constructors (FacadeBoundaryTest's rule), no one-off effect where a composition exists.
   Consult docs/card-sdk-language-reference.md for what already exists.
5. Note whether the card has a scenario test at all, and whether that test would actually
   fail if the ability were broken (an assertion on the real effect, not just "it resolved").

Return only, one block per card, nothing else:
CARD / VERDICT (ok|divergent|no-test|both) / FINDING (one line, or -) /
SEVERITY (gameplay|display|style|-) / SUGGESTED (one line fix, or -)
```

Aggregate: count verdicts, keep only `divergent` and `no-test` rows, and re-dispatch anything the
reviewer flagged as uncertain to a second reviewer with a different framing before you act on it.

## What the machines can check first

Run these before dispatching anybody — they're cheap and they shrink the human-judgement surface.

| Tool | What it proves | Limit |
|---|---|---|
| `just assay-differential --set <CODE>` | Assay's independent reading of each oracle text against the card we hand-wrote from the same text. A `DIVERGENT` row is a *semantic* disagreement — exactly the Stage 3 question, decided by a parser instead of a reviewer. | Only over the class the grammar reads whole. A modern set declines most cards, so this is a floor, not the pass. |
| `just assay-gate --set <CODE>` | Every oracle text in the set normalizes and re-prints invertibly. | Declines aren't failures — they're the coverage number. |
| `just check-backlog-implementations` | Every `[ ]` in the backlog is genuinely unimplemented. | Names only. |
| `CardLintTest` | Pipeline-variable dataflow, `ContextTarget` / `BoundVariable` resolution, choice-slot declarations. | Internal hygiene, not fidelity to the printed card. |
| `FacadeBoundaryTest` | Cards use the facades, not raw constructors. | Style, not semantics. |

**Read every `DIVERGENT` row from the differential.** The recipe's own comment says to classify each as
parser bug / card bug / known fold — and its history is that it found real bugs in hand-written cards
(the batch-trigger band exposed five at once). It is the single highest-yield DSL check in the repo.

## The token checklist

Tokens are the part of a set most likely to be quietly wrong, because nothing about a wrong token fails.
A token with no art doesn't render blank — it falls through to a Scryfall *name guess*
(`cards/named?exact=<name>`) and shows whatever printing comes back. That's how Arahbo's Foundations Cat
ended up showing Dominaria Remastered art: nothing was missing, so nothing failed.

**Resolution order** (`TokenArtRegistry`, mirrored by the token executors):

1. An explicit `imageUri` on the `CreateToken` effect — always wins.
2. This set's rows, for **the set the creating card was printed in** — hand-authored `MtgSet.tokenArt`
   ahead of synced `TokenArtData` (`mtg-sets/core/src/main/resources/tokens.json`, keyed by set code).
3. The engine-wide generic fallback by creature type (`TokenArt.IMAGES`).

Step 2 is the "correct creator" rule, and it's why art belongs on the set rather than the card: keying on
the creating card's printing is what makes a reprint mint the *reprint's* token. `resolve()` prefers
`sourcePrintingSetCode` (the printing the player actually brought) over the canonical
`sourceCardDefinitionId`.

Checks, in order:

```bash
just token-art-gaps     # -> backlog/token-art-gaps.md; must list nothing for your set
just test-class TokenArtCoverageTest
```

- **`token-art-gaps` is the real check.** `TokenArtCoverageTest` is only the floor — it proves nothing
  renders via a Scryfall name-guess. `token-art-gaps` proves the stronger property: no token of yours is
  showing *generic stand-in* art instead of its own set's. The file is generated; don't hand-edit it, and
  note it's global — read the rows for your set code.
- **Every token the set's cards can mint needs a row.** Enumerate with `TokenCreationSites.of(card)`, which
  walks the serialised card tree and so finds a `CreateToken` nested in a composite, a mode, a pipeline, a
  reflexive trigger, a granted ability or a `ConvertCountersToTokens` — a grep for `CreateToken` will not.
- **Synced rows are usually enough.** `just token-art-sync` regenerates `tokens.json` wholesale from
  Scryfall's `t<code>` token sets, so a modern set typically needs no hand-authored `tokenArt` at all. MSH
  is like this: 21 synced rows, no override. Hand-author only what the sync can't supply — mostly pre-2001
  sets, which have no `t<code>` set to draw from and fall back to `/images/tokens/`.
- **`imageUri` must be the Scryfall `normal` URL**, the whole token card. An `art_crop` URL still *works*
  but takes the legacy path: the client sees `/art_crop/` and draws the art inside a frame it generates
  itself, which is how a token with a real printed card ends up looking like a placeholder.
- **Hunt explicit `imageUri` on token effects** — resolution step 1 wins over everything, and
  `TokenArtCoverageTest` deliberately skips those sites, so they are a blind spot in both gates. It is the
  documented anti-pattern (`TokenPrinting` KDoc): baking art into the card mints the same art from every
  printing and is wrong the moment the card is reprinted. Beware the false positive — a card's own
  `metadata { imageUri = … }` is card art and entirely correct; only an `imageUri` argument *inside a token
  effect* is the smell.
- **Discriminators.** A bare `TokenPrinting("Cat", art)` matches every Cat the set mints. Pin `power` /
  `toughness` / `colors` only when the set prints two tokens sharing a name. One token printed with several
  illustrations is several rows differing only in `imageUri`; the engine deals them out in order.

Verdict block for a token reviewer, if you dispatch one:

```
Return only:
TOKEN / CREATED-BY (cards) / SOURCE (explicit|set-tokenArt|synced|generic-fallback) /
IMAGE (normal|art_crop|self-hosted|none) / VERDICT (ok|wrong-set|wrong-form|missing) / NOTE (one line)
```
