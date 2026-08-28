# Innistrad: Crimson Vow (VOW) — Mechanics

Counts are over the 272 booster cards (excluding basic lands and tokens), detected by
regex over front-face + back-face oracle text, so they are **approximate** — a payoff that
merely mentions "Blood token" is counted alongside the cards that make one. "Unimpl" = not
yet implemented as of this document (~77 cards done; see [`cards.md`](cards.md) for the live
checklist). Counts predate the in-flight card batch and will drift.

Engine-support column reflects whether the SDK/rules-engine already models the mechanic (so
cards using *only* supported mechanics need **no backend change** — pure `add-card` work).

## Set mechanics

| Mechanic | Cards | Unimpl | Engine support | Notes |
|----------|------:|-------:|----------------|-------|
| **Blood token** | ~30 | ~19 | ✅ `Effects.CreateBlood` | Artifact token: `{1}, {T}, Discard a card, Sacrifice: Draw a card.` 11 already done (e.g. Blood Fountain, Voldaren Epicure). |
| **Cleave** | 12 | 2 | ✅ `AlternativeCostType.CLEAVE` + `cleaveTarget`/`cleaveEffect` DSL | CR 702.148 — text-modifying alternative cost on instants/sorceries, modeled as a cast-mode branch (not string mutation). 10 authored (reference cards Alchemist's Gambit, Dig Up, Fierce Retribution, Path of Peril, Wash Away + this batch's Alchemist's Retrieval, Dread Fugue, Lunar Rejection, Parasitic Grasp, Winged Portent). 2 blocked on orthogonal sub-features — see [`engine-features-cleave-blocked.md`](engine-features-cleave-blocked.md): **Inspired Idea** (reduce max hand size by N) and **Lantern Flare** (`{X}` in the cleave cost). |
| **Training** | 9 | 9 | ❌ **GAP → [#1261](https://github.com/wingedsheep/argentum-engine/issues/1261)** | CR 702.149 — attack trigger gated on a co-attacker with greater power → +1/+1 counter; plus a "when this creature trains" payoff hook (702.149c). Structural analog: Mentor + Decayed. |
| **Exploit** | 9 | 9 | ❌ **GAP → [#1260](https://github.com/wingedsheep/argentum-engine/issues/1260)** | CR 702.110 — ETB "may sacrifice a creature" + a paired "when this creature exploits a creature" payoff (702.110b, the crux). Analog: Casualty's reflexive "when you do" trigger. Also surfaces as blocked trigger `WhenAPermanentExploitsAPermanent` (×9). |
| **Disturb** | ~13 | ~7 | ✅ `disturb("{cost}")` DSL + `DisturbCasts` | CR 702.146 — cast the back face from your graveyard, then exile. Shipped since this file was written: the DSL sits on top of the existing transform machinery, and `DisturbCasts` owns the graveyard cast. Reference cards: Lantern Bearer, Twinblade Geist, Kindly Ancestor, Drogskol Infantry, Binding Geist, Mischievous Catgeist, Distracting Geist. |
| **Daybound / Nightbound** | ~14 | ~14 | ✅ `DayNightService` + `DayNightDsl` | CR 702.145 — day/night designation + werewolf-style DFC flips keyed on spells-cast-per-turn. Shipped since this file was written: `DayNightService` holds the designation, `BeginningPhaseManager` advances it, and `DayNightMechanicScenarioTest` covers the flips. The remaining cards are authoring work, not engine work. |

**Transform / DFC machinery** — ✅ present (`TransformEffect`, `ExileAndReturnTransformedEffect`,
`ReturnSelfFromZoneTransformedEffect`). The *effect* layer that flips a permanent's face exists and is
reused by other sets, and the two *keyword layers* above it — Disturb's graveyard-cast and
Daybound/Nightbound's day/night trigger — have since shipped too.

## Evergreen / returning keywords present

Flying (~30), Menace (~8), Defender (~5), Reach (~4), Vigilance (~4), Flash (~4),
Lifelink (~3), Deathtouch (~3), Haste (~3), First strike (~3), Ward (~2),
Double strike (1), Hexproof (1). Plus Equip (~5), Mill (~8), Scry (~1), Investigate,
Fight (~1), and +1/+1 counters (~29). **All engine-supported.**

## Backend-change assessment

Two headline VOW mechanics still have open engine work items (route through `add-feature`):

- **Training** ([#1261](https://github.com/wingedsheep/argentum-engine/issues/1261), CR 702.149) —
  ×9 cards. Attack trigger + power comparison + "when it trains" hook.
- **Exploit** ([#1260](https://github.com/wingedsheep/argentum-engine/issues/1260), CR 702.110) —
  ×9 cards (+ the `WhenAPermanentExploitsAPermanent` trigger, ×9). ETB optional sacrifice + paired
  "when it exploits" payoff.

**Cleave** ([#1259](https://github.com/wingedsheep/argentum-engine/issues/1259), CR 702.148) is now
**implemented** — `AlternativeCostType.CLEAVE` + the `cleaveTarget`/`cleaveEffect` DSL model it as a
cast-mode branch (not text mutation). 10 of 12 cleave cards are authored. The remaining 2 are blocked
not by cleave itself but by orthogonal sub-features (documented at
[`engine-features-cleave-blocked.md`](engine-features-cleave-blocked.md)):
**Inspired Idea** needs a reduce-max-hand-size-by-N effect; **Lantern Flare** needs `{X}` support in
the cleave-cost enumerator.

Training and Exploit are the top remaining entries on `just coverage-gaps --set VOW`'s BLOCKED
leaderboard (×9 each), so clearing them unlocks the most cards.

**Disturb and Daybound / Nightbound are no longer gaps.** Both keyword layers shipped after this
file was written — see the table above for the entry points. Their remaining cards are pure
`add-card` authoring. They are absent from the coverage leaderboard because their DFCs land in the
tool's "unmatched in mtgish" bucket (name-join misses), not because they're unsupported.

**Smaller blocked capabilities** (from `coverage-gaps`, lower-volume — assess per card, may be pure
`add-feature` one-offs): `SetPT` layer effect (×4), `RemoveCounters` cost/action (×3),
`WhenAPlayerPlaysALand` trigger (×2), `PermanentDoesntUntapDuringControllersNextUntap` (×2),
characteristic-defining `CDA_Power`/`CDA_Toughness` (×2 each), `AddCardtype` (×2), and a tail of ×1
items.

**Everything else is pure `add-card` authoring** — Blood-token cards, the returning/evergreen keyword
cards, and standard effects. Suggested order: clear the Blood/evergreen single-faced cards first, then
land the Training/Exploit features (highest unlock), and take the Disturb / Daybound DFCs as ordinary
authoring work now that both keyword layers exist.
