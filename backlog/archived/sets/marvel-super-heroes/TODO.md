
Create the rest of the MSH cards.

- [x] Big groups of cards belonging together, minimum 3 cards. In one branch.
- [x] Most challenging cards to implement. Max 5 different. Each in their own branch.
- [x] Rest of the cards, grouped logically, minimum 3 and maximum 5 in each branch.

The branches should be stacked on top, so that they will be merged cleanly.

Write the steps to be done in this file. Use subagents to implement the cards using the /add-card to not polute the context of the main runner.

## Plan

Run `msh-rest` · ledger `.claude/loop-runs/msh-rest.md` · 26 cards missing (25 queued, 1 skipped).
**Base branch for every unit: `loop-msh-u10`** (tip `4cb589695a`) — units stack on it, branches
`loop-msh-u11` … `loop-msh-u21`.

Cohesive groups first, then the 5 hardest as solos, then the rest in themed batches of 3–5.

- [x] **u11 batch** — Loki cycle: Kid Loki · Loki Laufeyson · Loki, God of Mischief
- [x] **u12 batch** — two-colour legends: Bullseye, Death Dealer · Captain America, Living Legend · Cloak and Dagger, Entwined
- [x] **u13 batch** — activated / restricted-mana abilities: Powerful Broker · Ronin, Shadow Stalker · Shang-Chi, Master of Kung Fu
- [x] **u14 batch** — mono-white legends: Captain Mar-Vell, Space-Born · Invisible Woman, Sue Storm · Nick Fury, Agent of S.H.I.E.L.D. · Red Guardian, Super-Soldier
- [x] **u15 batch** — spells-you-cast-matter: Ms. Marvel, Kamala Khan · Namor the Sub-Mariner · Storm, Windrider · The Scarlet Witch
- [x] **u16 batch** — noncreature (Plan / Saga / sorcery): Doom Reigns Supreme · Evil's Thrall · World War Hulk
- [x] **u17 solo** — Hawkeye, Master Marksman
- [x] **u18 solo** — Leader, Super-Genius
- [x] **u19 solo** — Wolverine, Fierce Fighter
- [x] **u20 solo** — Kang the Conqueror
- [x] **u21 solo** — Wonder Man, Hollywood Hero
- [x] **u22 solo** — Baron Helmut Zemo — un-skipped in phase 2 and shipped. Boast is **CR 702.142**;
  the 702.135 recorded here was wrong (that number is afterlife).


  One thing I've recorded as a follow-up for you rather than letting the loop grow into it: the review found a real
  last-known-information gap in evaluateDynamicCap — a dynamic power cap reads the source's base power after it leaves the
  battlefield instead of LKI (CR 608.2h/113.7a). It affects Loki's rider but the fix is engine work, so it's noted in the ledger as
  an add-feature job, not bolted onto a card PR.