# Fallen Empires (FEM) — Mechanics

Fallen Empires has **no named keyword mechanics of its own**. Its identity is five warring factions,
each expressed as an unnamed theme: Thrull sacrifice (black), Thallid spore counters (green),
Homarid tide counters (blue), Goblin/Orc/Dwarf attrition (red), and Icatian tokens-and-counters
(white). The set also prints two full land cycles and a large "sacrifice for value" artifact bloc.

A box is ticked when the engine already models the mechanic (an SDK primitive exists and a card in the
corpus uses it). An unticked box is `add-feature` work that blocks the cards listed under it.

---

## Set themes

### - [x] Sacrifice a permanent as an activation cost (24 cards)

The set's spine. Almost every faction turns a permanent into an effect by sacrificing it as part of an
activation cost (CR 118.8 — costs are paid on activation, before anyone can respond) or as an
additional cost to cast (CR 601.2f/h).

**Engine support:** ✅ `Costs.Sacrifice*` — `SacrificeSelf`, `SacrificePermanent(filter)`,
`Costs.additional.SacrificePermanent`. Used throughout the corpus.

Cards: Aeolipile, Armor Thrull, Balm of Restoration, Basal Thrull, Conch Horn, Delif's Cone,
Elven Lyre, Elvish Farmer, Goblin Chirurgeon, Goblin Grenade, Goblin Warrens, Heroism,
Homarid Spawning Bed, Icatian Moneychanger, Implements of Sacrifice, Raiding Party, Soul Exchange,
Thallid Devourer, Thelonite Druid, Thelonite Monk, Thrull Retainer, Tourach's Gate,
Dwarven Ruins / Ebon Stronghold / Havenwood Battleground / Ruins of Trokair / Svyelunite Temple
(the sacrifice-land cycle).

### - [ ] Spore counters and Saprolings (7 cards)

The Thallid engine: "At the beginning of your upkeep, put a spore counter on this creature. Remove
three spore counters from this creature: <effect>." Ordinary counters (CR 122) plus an upkeep trigger
and a remove-N-counters activation cost.

**Engine support:** ✅ `Counters.SPORE` / `CounterType.SPORE` already exist, as does
`Costs.RemoveCounterFromSelf(Counters.SPORE, 3)` and `Triggers.AtBeginningOfYourUpkeep`. The Saproling
token needs registering with FEM art.

Cards: Elvish Farmer, Feral Thallid, Fungal Bloom, Night Soil, Spore Flower, Thallid,
Thallid Devourer, Thorn Thallid

### - [ ] Storage lands (5 cards)

The "Vault" cycle — enters tapped, may choose not to untap, accrues a storage counter each upkeep while
tapped, and dumps them all for coloured mana. The interesting halves are the optional-untap replacement
(CR 502.2) and a mana ability whose cost removes *any number* of counters and whose output scales with
how many were removed.

**Engine support:** ✅ `Counters.STORAGE`, the may-choose-not-to-untap static (see Preacher,
Ashnod's Battle Gear), and a dynamic-amount mana ability (see City of Shadows). The "remove any
number, add that much" cost/amount pairing is the piece to check per card.

Cards: Bottomless Vault, Dwarven Hold, Hollow Trees, Icatian Store, Sand Silos

### - [ ] "Attacks and isn't blocked" triggers (6 cards)

FEM's signature combat template: a trigger that fires when a creature is attacking and unblocked, whose
effect trades the creature's combat damage away for something else ("it assigns no combat damage this
turn").

**Engine support:** ✅ `Triggers` has an attacks-and-isn't-blocked shape (Murk Dwellers, Merchant Ship).
The "assigns no combat damage this turn" rider needs checking against the existing prevention/assignment
vocabulary.

Cards: Delif's Cone, Delif's Cube, Farrel's Mantle, Farrel's Zealot, Mindstab Thrull, Necrite

### - [ ] Sacrifice-lands and mana-creature burnout (8 cards)

Two cycles: lands that enter tapped, tap for one mana, or sacrifice for two; and the
"{1}: Add {C}. If this ability has been activated four or more times this turn, sacrifice this creature
at the beginning of the next end step" pair. The latter needs a per-turn activation count (CR 608.2)
feeding a delayed sacrifice.

**Engine support:** ⚠️ partial. Lands: ✅ plain mana abilities + `Costs.SacrificeSelf`. The
activation-count burnout is the piece to verify — it needs "number of times this ability has been
activated this turn" as a condition.

Cards: Dwarven Ruins, Ebon Stronghold, Havenwood Battleground, Ruins of Trokair, Svyelunite Temple,
Farrelite Priest, Initiates of the Ebon Hand, Basal Thrull

### - [ ] Asymmetric stat counters beyond ±1 (3 cards)

FEM prints +1/+2, +2/+2 and -2/-2 counters. CR 122.1a defines a +X/+Y counter generally, but the engine
enumerates only the six ±1 kinds.

**Engine support:** ❌ `CounterType` has `PLUS_ONE_PLUS_ZERO` / `PLUS_ZERO_PLUS_ONE` /
`MINUS_ONE_MINUS_ZERO` / `MINUS_ZERO_MINUS_ONE` and `EffectApplicator` sums exactly those. Adding
`PLUS_ONE_PLUS_TWO`, `PLUS_TWO_PLUS_TWO` and `MINUS_TWO_MINUS_TWO` follows the same pattern but is an
SDK + engine change (`add-feature`).

Cards: Armor Thrull, Ebon Praetor, Soul Exchange

### - [ ] Tide counters (2 cards)

A counter that accrues each upkeep and whose *exact* count switches a static effect on and off, then
resets at four or more. Needs an exact-count condition (CR 613 layer 7c) rather than a threshold.

**Engine support:** ❌ no `TIDE` counter type; the exactly-N-counters static condition needs checking.

Cards: Homarid, Tidal Influence

### - [ ] "You may choose not to untap this permanent" (6 cards)

An optional skip of the untap step (CR 502.2), usually paired with an effect that lasts "for as long as
this remains tapped".

**Engine support:** ✅ the static exists (Preacher, Ashnod's Battle Gear, Tawnos's Weaponry).

Cards: Bottomless Vault, Dwarven Hold, Hollow Trees, Icatian Store, Sand Silos, Seasinger,
Spirit Shield, Zelyon Sword, Deep Spawn, Homarid Warrior

### - [ ] Damage prevention (7 cards)

Old-border prevention shields — "prevent the next N damage", "prevent all combat damage that would be
dealt this turn", "prevent all combat damage that would be dealt to and dealt by it".

**Engine support:** ✅ prevention effects exist; the *conditional* forms (Heroism's "unless its
controller pays {2}{R}", Tidal Flats) need per-card checking.

Cards: Balm of Restoration, Combat Medic, Elvish Scout, Heroism, Spore Cloud, Spore Flower, Tidal Flats

---

## Keyword mechanics

Every keyword the set uses is already modelled.

### - [x] Regenerate (5 cards)

CR 701.19 — a replacement shield that taps the permanent, removes it from combat, and heals damage
instead of destroying it.

**Engine support:** ✅ `Effects.Regenerate`.

Cards: Delif's Cube, Draconian Cylix, Feral Thallid, Goblin Chirurgeon, Thrull Retainer

### - [x] Enchant (4 cards)

CR 702.5 — an Aura's restriction on what it can be attached to.

**Engine support:** ✅ `auraTarget` in the card DSL.

Cards: Farrel's Mantle, Merseine, Thrull Retainer, Tourach's Gate

### - [x] First strike (3 cards)

CR 702.7 — deals combat damage in the first combat damage step.

**Engine support:** ✅ `Keyword.FIRST_STRIKE`.

Cards: Ebon Praetor, Icatian Skirmishers, Vodalian Knights

### - [x] Trample (3 cards)

CR 702.19 — excess combat damage is assigned to the defending player or battle.

**Engine support:** ✅ `Keyword.TRAMPLE`.

Cards: Deep Spawn, Ebon Praetor, Orgg

### - [x] Banding (2 cards)

CR 702.22 — attackers may be grouped into a band; banding inverts who assigns combat damage.

**Engine support:** ✅ `Keyword.BANDING`, wired through `AttackPhaseManager` / `CombatDamageManager`
(Pikemen, Knights of Thorn, War Elephant).

Cards: Icatian Phalanx, Icatian Skirmishers, Icatian Infantry (grants it)

### - [x] Protection (2 cards)

CR 702.16 — DEBT: can't be damaged, enchanted/equipped, blocked, or targeted by the quality.

**Engine support:** ✅ `Keyword.PROTECTION`.

Cards: Order of Leitbur, Order of the Ebon Hand

### - [x] Defender (1 card)

CR 702.3 — can't attack.

**Engine support:** ✅ `Keyword.DEFENDER`.

Cards: Vodalian War Machine

### - [x] Landwalk / Islandwalk / Mountainwalk (2 cards)

CR 702.14 — unblockable while the defending player controls a land of the named type.

**Engine support:** ✅ `Keyword.ISLANDWALK`, `Keyword.MOUNTAINWALK`.

Cards: Goblin Flotilla, River Merfolk (grants mountainwalk)

### - [x] Shroud (3 cards)

CR 702.18 — can't be the target of spells or abilities.

**Engine support:** ✅ `Keyword.SHROUD`.

Cards: Deep Spawn, Homarid Warrior, Svyelunite Priest

### - [x] Menace (1 card)

CR 702.111 — can't be blocked except by two or more creatures.

**Engine support:** ✅ `Keyword.MENACE`.

Cards: Goblin War Drums (grants it)

### - [x] Mill (1 card)

CR 701.17 — put the top N cards of a library into its owner's graveyard.

**Engine support:** ✅ `Patterns.Library` mill recipes.

Cards: Deep Spawn
