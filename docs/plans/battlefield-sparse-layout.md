# Battlefield layout on sparse boards

Status: implemented — `web-client/src/components/game/board/battlefieldLayout.ts` (pure solver),
`usePooledBattlefieldLayout.ts`, `useBoardGroups.ts`. Scope: `web-client` only — no server or engine
change. Numbers below are the implementation's (integer widths), pinned by `battlefieldLayout.test.ts`.

Constraint (decided 2026-08-29): **card positions stay where they are.** Creatures/planeswalkers
stay in the front row next to the HUD, lands/other stay in the back row at the outer edge, the
opponent stays mirrored. Only card *size* changes with the number of cards on the board.

## Problem

With few permanents in play the battlefield renders cards at ~65 px wide on a 1546×795 viewport
and leaves ~90 % of the width empty. A crowded board on the same viewport renders cards at the
*same* size — the width is only used once a row holds 10+ stacks. Card size does not scale with
the number of cards until the board is crowded, and never scales up.

## Why it happens

Battlefield card size is `min(heightCap, widthCap, SLOT_MAX_CARD_WIDTH=200)` computed per player
slot by `useSlotSizedResponsive` (`web-client/src/components/game/board/shared.ts:86-302`). On a
desktop two-player screen the **height cap binds for every board up to ~10 stacks per row**; the
width cap is irrelevant until then. Given fixed positions, the height budget is the only lever, and
today it is spent badly:

1. **An empty row costs a full line.** The search in `shared.ts:168-196` starts at `front=1, back=1`
   and both rows reserve `rowMinHeight(1)` unconditionally (`Battlefield.tsx:520-535`).
2. **Overheads are sized from constants, not from the card.** `dividerMargin` is
   `max(10, 0.1 × baseCardHeight)` where the base card is the 125-wide desktop card — 18 px per side
   for a card that actually renders 92 px tall. `breathing` is 10 % of the *slot* (up to 48 px).
   `dividerSpace` is subtracted even when the divider does not render (`Battlefield.tsx:500-511`).
3. **Both players get the same slot height** — grid rows 2 and 4 are equal `1fr`
   (`GameBoard.tsx:892-900`, `styles.ts:27-28`) — whatever each player holds, so a player with one
   land and a player with fourteen permanents split the height 50/50.

For the 290 px-tall slot measured from the 1546×795 dev screenshot:

```
breathing = clamp(round(290 × 0.10), 12, 48) = 29     dividerSpace = 24 + 2 × 18 = 60
two lines = floor((290 − 60 − 29) / (2 × 1.4 + 0.224)) = 66 px   ← today, every board until it wraps
```

## Design

Five steps, each independently shippable, each a pure function of `(slot size, per-row counts)`.
Steps 1–2 change only `shared.ts` + `Battlefield.tsx`; step 3 lifts the solve to `GameBoard.tsx`;
step 4 is optional; step 5 is CSS. All numbers below are for the reference 1200×290 slot.

### Invariants kept

- Row order and group placement are unchanged; only `battlefieldCardWidth` moves.
- Nothing bleeds over the center HUD: every candidate is still sized from the measured slot with a
  reserved gap to the HUD.
- Both players render at the same card width (`styles.ts:27-28`). Step 3 keeps this by solving one
  width for both slots.
- No stored mode, no hysteresis: a card entering can change the size, and step 5 animates it.
- `SLOT_MAX_CARD_WIDTH = 200` stays; badges, attachment peeks and stack offsets already scale from
  `battlefieldCardWidth`.

### Step 1 — an empty row costs nothing

`useSlotSizedResponsive`: when `frontRowCount === 0` (or `backRowCount === 0`) search that row with
`lines = 0`, and drop the divider from the height budget whenever either row is empty (matching when
it renders). `Battlefield.tsx`: render the empty row at a fixed 8 px `minHeight` instead of
`rowMinHeight(1)` — keep the element so `data-zone` spotlights and DOM order are unchanged.

```
lands only : floor(290 / (1.4 + 0.112 + 0.21)) = 168 px      (66 today, 2.5×)
```

Every turn-1 to turn-3 board. Zero visual change once both rows are populated.

### Step 2 — overheads scale with the card, not with constants

Replace the three fixed terms with functions of the card being solved for (`h = 1.4w`):

| term | today | proposed |
|------|-------|----------|
| divider margin (each side) | `max(10, 0.1 × baseH)` = 18 | `max(6, 0.1h)` |
| breathing toward HUD | `clamp(0.10 × slotH, 12, 48)` | `clamp(0.15h, 12, 48)` |
| row padding | `0.08h` per row (unchanged) | `0.08h` per *present* row |

The budget stays linear in `w`, so the search is unchanged except the divisor:

```
both rows, 1 line each :
  290 − 24 ≥ w × (2 × 1.4 + 2 × 0.112 + 2 × 0.14 + 0.21)  →  w = 76 px   (66 today, 1.15×)
```

Small, but it applies to every populated board, and it makes the solve monotone in card count: on a
crowded board (small `w`) the overheads shrink with the cards instead of eating a fixed 89 px.

### Step 3 — pooled, demand-weighted slot heights (two-player)

Replace the equal `minmax(0, 1fr)` rows 2 and 4 with heights derived from each player's line demand.
One hook in `GameBoard` measures the pooled height (`row2 + row4`) and both players' counts, then
finds the largest common width `w` such that

```
Σ players ( lines·1.4w + (lines − rows)·cardGap + rows·0.112w + [both rows]·(24 + 0.28w) + breathing(w) ) ≤ pooledH
```

subject to each player's width caps, and gives each slot the height its own terms need. Clamp the
split to `[0.35, 0.65]` of the pooled height so the center HUD never drifts more than ~15 % from
where it is today — that clamp is what keeps "positions roughly the same" for the HUD and the rows
around it. Rows become `minmax(0, hA px) auto minmax(0, hB px)`.

```
lands only  vs  6 creatures + 6 lands  (580 px pooled):
  3.514w + 24 ≤ 0.65 × 580  (the clamp binds)   →  w = 101 px, slots 203 / 377   (66 today, 1.5×; steps 1–2 alone: 76)
2 lines  vs  3 lines  (24 creatures + 6 lands, wrapping to two creature lines on a 1200 px board):
  today 46 px (it wraps today too)  →  52 px per slot (steps 1–2)  →  61 px pooled   (1.3×)
```

So pooling helps both ends: the sparse side gives height to the crowded side, and a board that wraps
gets the line it needs without shrinking below the other player's size. `Battlefield` takes its
layout as a prop; `useSlotSizedResponsive` remains the multiplayer per-cell path (strip cells sit
side by side and do not pool). This is the only step needing a two-pass measure — if it proves
fiddly, ship 1, 2 and 5 without it.

### Step 4 — optional: back-row scale

A single knob `BACK_ROW_SCALE ∈ [0.7, 1]` renders the land/other row at `r × w`. Positions are
untouched; only the lands are smaller than the creatures. At `r = 0.7` on the reference slot,
creatures go 76 → 87 px and lands 76 → 61 px. Recommendation: ship the knob at `1.0` and decide
from a screenshot walk — it is a taste call (a second card size on the board) for a 1.16× gain.

### Step 5 — size transitions

`transition: width 180ms ease, height 180ms ease` on the battlefield card container; `CardStack`
layer offsets derive from width so they follow. Off under `prefers-reduced-motion`. The arrow
overlays already poll rects every 100 ms (`TargetingArrows.tsx:338`, `CombatArrows.tsx:613`,
`SoulbondBonds.tsx:215`) and drag-drop resolves with `elementFromPoint` live, so both follow a
transition without change. Confirm `DrawAnimations.tsx` / `DamageAnimations.tsx` measure their
endpoints after layout — otherwise gate the transition off while an animation is in flight.

### Extract a pure solver first

Move the search out of the hook into `solveSlotLayout` / `solvePooledLayout` in a new
`board/battlefieldLayout.ts` — plain data in, plain data out — with the hook reduced to measure +
call. The hook has no tests today (only `attachmentStackLayout` in `shared.test.ts` is covered);
this is what makes steps 1–4 unit-testable in `vitest` and gives the pooled and per-cell solves one
implementation. Fold the duplicated geometry constants (tapped footprint `1.4w + 8`, stack offset
12/18, divider margins, row padding) into that module so they have one home.

Tests to write, reference 1200×290 slot unless stated:

- empty front row → one line, no divider, width 168
- 2 lands + 1 creature → two lines, width 76
- 6 lands **tapped** + 6 creatures → still 76 (height-bound; tapped footprint irrelevant until wrap)
- 60 stacks per row on a 360×400 phone slot → the floor (32 px, four lines per row)
- pooled: lands-only vs 6 + 6 over 580 px → width 101, heights 203 / 377 (the 0.35 clamp binds)
- pooled: 1 + 1 vs 24 creatures + 6 lands on 1200 wide → three lines on the crowded side, width 61 (46 today)

## Rejected

- **Merging lands and creatures onto one line** on sparse boards (up to 2.4× on the reference slot).
  Rejected 2026-08-29: it moves groups around, which confuses existing players. The height ceiling
  above is the price of that decision, and the design accepts it.
- **A user-facing zoom slider** — none exists; the solver should make it unnecessary.
- **Shrinking the hand reservation** (220 px desktop) — the fan needs full height for even two
  cards, and a 150 → 125 hand card would buy ~5 px of battlefield width.

## Verification

`npm test` and `npm run lint` in `web-client/` (the new solver tests) plus `just client-typecheck`,
then a screenshot walk of the dev scenarios used to write this doc — sparse (3 vs 2),
stacked-heavy (18 vs 14 identical), varied (22 vs 16 distinct with attachments), lopsided
(1 land vs 12), a 390×844 phone viewport — plus one combat with targeting arrows while a card size
transition is in flight.
