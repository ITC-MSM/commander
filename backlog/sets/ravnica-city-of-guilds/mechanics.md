# Ravnica: City of Guilds (RAV) — Mechanics

Source inventory at baseline `4f09fec7e2`, with in-progress implementation noted below.
Support here means existing vocabulary, not a completed behavioral audit of every listed card.

A box is ticked when the engine already models the mechanic (an SDK primitive exists and a card in the corpus uses it). An unticked box is missing or unverified functionality; composition must be tried before adding engine types.

---

## Keyword mechanics

### - [x] Flying (26 cards)

Flying creatures require flying or reach blockers.

**Engine support:** `Keyword.FLYING`

Cards: Belltower Sphinx, Birds of Paradise, Blazing Archon, Cerulean Sphinx, Conclave Equenaut, Courier Hawk, Divebomber Griffin, Drake Familiar, Drift of Phantasms, Firemane Angel, Hunted Dragon, Hunted Lammasu, Keening Banshee, Moroii, Nullstone Gargoyle, Razia, Boros Archangel, Screeching Griffin, Skyknight Legionnaire, Snapping Drake, Stinkweed Imp, Surveilling Sprite, Szadek, Lord of Secrets, Tattered Drake, Vulturous Zombie, Wizened Snitches, Woebringer Demon

### - [x] Mill (19 cards)

Move cards from the top of a library into its graveyard. Dredge uses milling as part of its replacement, not as a separate draw trigger.

**Engine support:** The library mill pipeline supports ordinary milling and the composed milling step of dredge.

Cards: Belltower Sphinx, Darkblast, Duskmantle, House of Shadow, Glimpse the Unthinkable, Golgari Brownscale, Golgari Grave-Troll, Golgari Thug, Grave-Shell Scarab, Greater Mossdog, Induce Paranoia, Life from the Loam, Moldervine Cloak, Necroplasm, Nightmare Void, Psychic Drain, Shambling Shell, Stinkweed Imp, Szadek, Lord of Secrets, Vedalken Entrancer

### - [x] Enchant (17 cards)

An Aura restricts what it can enchant and normally targets that object when cast.

**Engine support:** `CardBuilder.auraTarget`; special casting and return restrictions still need individual review.

Cards: Breath of Fury, Clinging Darkness, Conclave's Blessing, Dream Leash, Faith's Fetters, Fists of Ironwood, Flickerform, Flight of Fancy, Followed Footsteps, Galvanic Arc, Instill Furor, Mark of Eviction, Moldervine Cloak, Necromantic Thirst, Pollenbright Wings, Stasis Cell, Strands of Undeath

### - [x] Convoke (15 cards)

Untapped creatures help pay generic or matching colored spell costs.

**Engine support:** `Keyword.CONVOKE`, `AlternativePaymentHandler`, and `ManaCost.reduceByConvoke`

Cards: Autochthon Wurm, Chant of Vitu-Ghazi, Chord of Calling, Conclave Equenaut, Conclave Phalanx, Conclave's Blessing, Devouring Light, Gather Courage, Guardian of Vitu-Ghazi, Hour of Reckoning, Overwhelm, Root-Kin Ally, Scatter the Seeds, Siege Wurm, Sundering Vitae

### - [x] Transmute (13 cards)

Pay mana and discard this card from hand, at sorcery timing, to search for and reveal a card of the same mana value, put it in hand, and shuffle.

**Engine support:** `transmute(cost)` composes hand activation, discard-self, sorcery timing, and library search. Seven authored cards and a reanimation/copy response regression verify the composition.

Cards: Brainspoil, Clutch of the Undercity, Dimir House Guard, Dimir Infiltrator, Dimir Machinations, Dizzy Spell, Drift of Phantasms, Ethereal Usher, Grozoth, Muddle the Mixture, Netherborn Phalanx, Perplex, Shred Memory

### - [x] Dredge (12 cards)

Replace a draw by milling the specified number of cards and returning this graveyard card to hand; require enough cards in the library.

**Engine support:** `KeywordAbility.dredge(N)` uses intrinsic graveyard sources in the shared draw-replacement pipeline. Sufficient-library checks, sequential draws, nested decisions, repeated declines, and saved-game reloads are covered. Remaining cards need their other abilities completed; dredge itself is implemented.

Cards: Darkblast, Golgari Brownscale, Golgari Grave-Troll, Golgari Thug, Grave-Shell Scarab, Greater Mossdog, Life from the Loam, Moldervine Cloak, Necroplasm, Nightmare Void, Shambling Shell, Stinkweed Imp

### - [x] Radiance (10 cards)

Affects the target and other objects sharing a color with it, using colors at resolution. Colorless objects share no colors.

**Engine support:** `sharingColorWith(EntityReference.Target(0))`, target exclusion, and group iteration; Bathe in Light demonstrates the composition. Brightflame additionally needs actual-damage accounting.

Cards: Bathe in Light, Brightflame, Cleansing Beam, Incite Hysteria, Leave No Trace, Rally the Righteous, Surge of Zeal, Wojek Apothecary, Wojek Embermage, Wojek Siren

### - [x] Regenerate (9 cards)

Create a shield that replaces the next applicable destruction, tapping the creature, removing damage and removing it from combat.

**Engine support:** Existing regeneration effects and replacement handling; see Votary of the Conclave.

Cards: Dimir House Guard, Gaze of the Gorgon, Golgari Grave-Troll, Hunted Troll, Loxodon Hierarch, Strands of Undeath, Tattered Drake, Votary of the Conclave, Woodwraith Strangler

### - [x] Defender (7 cards)

The creature cannot attack unless another effect permits it.

**Engine support:** `Keyword.DEFENDER`

Cards: Benevolent Ancestor, Carven Caryatid, Drift of Phantasms, Grozoth, Junktroller, Tidewater Minion, Torpid Moloch

### - [x] Trample (5 cards)

Combat damage assignment can carry excess damage beyond blockers.

**Engine support:** `Keyword.TRAMPLE`

Cards: Autochthon Wurm, Gleancrawler, Hunted Horror, Mindleech Mass, Siege Wurm

### - [x] Vigilance (5 cards)

Attacking does not cause the creature to tap.

**Engine support:** `Keyword.VIGILANCE`

Cards: Courier Hawk, Guardian of Vitu-Ghazi, Nightguard Patrol, Oathsworn Giant, Razia, Boros Archangel

### - [x] Equip (4 cards)

Activate at sorcery timing to attach equipment to a creature you control.

**Engine support:** `CardBuilder.equipAbility`; Sunforger needs an unattach cost independently.

Cards: Grifter's Blade, Pariah's Shield, Peregrine Mask, Sunforger

### - [x] Haste (4 cards)

The creature can attack and use tap-symbol abilities without waiting a turn.

**Engine support:** `Keyword.HASTE`

Cards: Goblin Fire Fiend, Hunted Dragon, Razia, Boros Archangel, Skyknight Legionnaire

### - [x] First strike (3 cards)

Deals combat damage in the first combat-damage step.

**Engine support:** `Keyword.FIRST_STRIKE`

Cards: Boros Recruit, Firemane Angel, Nightguard Patrol

### - [x] Fear (2 cards)

Only artifact creatures and black creatures can block it.

**Engine support:** `Keyword.FEAR`

Cards: Dimir House Guard, Undercity Shade

### - [x] Landfall (2 cards)

Trigger when a land you control enters.

**Engine support:** `Triggers.entersBattlefield` with a controlled-land filter; Stone-Seeder Hierophant.

Cards: Stone-Seeder Hierophant, Vinelasher Kudzu

### - [x] Reach (2 cards)

The creature can block flying creatures.

**Engine support:** `Keyword.REACH`

Cards: Goliath Spider, Selesnya Sagittars

### - [x] Double strike (1 card)

Deals combat damage in both combat-damage steps.

**Engine support:** `Keyword.DOUBLE_STRIKE`

Cards: Boros Swiftblade

### - [x] Flash (1 card)

The card can be cast with instant timing.

**Engine support:** `Keyword.FLASH`

Cards: Grifter's Blade

### - [x] Forestwalk (1 card)

Cannot be blocked if the defending player controls the corresponding basic land type.

**Engine support:** `Keyword.FORESTWALK`

Cards: Chorus of the Conclave

### - [x] Islandwalk (1 card)

Cannot be blocked if the defending player controls the corresponding basic land type.

**Engine support:** `Keyword.ISLANDWALK`

Cards: Grayscaled Gharial

### - [x] Mountainwalk (1 card)

Cannot be blocked if the defending player controls the corresponding basic land type.

**Engine support:** `Keyword.MOUNTAINWALK`

Cards: Goblin Spelunkers

### - [x] Swampwalk (1 card)

Cannot be blocked if the defending player controls the corresponding basic land type.

**Engine support:** `Keyword.SWAMPWALK`

Cards: Sewerdreg

## Recurring set themes

### - [x] Off-color mana spent riders (8 cards)

Resolution bonuses depend on colors actually spent casting the spell, including alternative payment interactions. Existing mana-spent conditions model these riders.

Cards: Boros Fury-Shield, Dryad’s Caress, Flash Conscription, Induce Paranoia, Ribbons of Night, Rolling Spoil, Seed Spark, Vigor Mortis.

### - [x] Saproling production (8 cards)

Creature tokens fuel convoke, creature-tapping costs, and sacrifice outlets. Existing token creation composes with triggers and activated abilities; set token art must still be checked during verification.

Cards: Bramble Elemental, Fists of Ironwood, Golgari Germination, Pollenbright Wings, Scatter the Seeds, Selesnya Evangel, Selesnya Guildmage, Vitu-Ghazi, the City-Tree.

### - [x] Off-color activated abilities (6 cards)

Monocolored creatures reward access to another guild color through ordinary colored activation costs.

Cards: Mortipede, Ordruun Commando, Roofstalker Wight, Screeching Griffin, Tattered Drake, Votary of the Conclave.

### - [x] Opponent compensation creatures (5 cards)

Efficient creatures create tokens for an opponent on entry. Existing opponent-directed token creation supports this cycle.

Cards: Hunted Dragon, Hunted Horror, Hunted Lammasu, Hunted Phantasm, Hunted Troll.

### - [x] Creature-tapping activation costs (5 cards)

Spend untapped creatures without requiring their own tap-symbol abilities. Existing tap-permanent costs handle these choices.

Cards: Glare of Subdual, Nullmage Shepherd, Root-Kin Ally, Sandsower, Selesnya Evangel.

### - [x] Bounce lands (4 cards)

Enter tapped, return a controlled land, and tap for two guild colors. Existing entry triggers, tapped entry, and mana abilities compose the cycle.

Cards: Boros Garrison, Dimir Aqueduct, Golgari Rot Farm, Selesnya Sanctuary.

### - [x] Shock lands (4 cards)

Two basic land types and an optional life payment replacing tapped entry. Existing entry-replacement support models the cycle.

Cards: Overgrown Tomb, Sacred Foundry, Temple Garden, Watery Grave.

### - [x] Signets (4 cards)

Pay one mana and tap to produce two guild colors. Existing mana costs and mana production model the cycle.

Cards: Boros Signet, Dimir Signet, Golgari Signet, Selesnya Signet.

## Remaining card-specific investigations

These are source-backed leads, not claims that every card needs a new executor.

| Cards | Required investigation or capability |
|---|---|
| Mausoleum Turnkey | Opponent-chosen targets on triggers: TriggerProcessor currently substitutes the controller and CardLinter rejects the unsupported shape. |
| Quickchange | Choice of any nonempty subset of colors; the current color decision is singular. |
| Dream Leash | Casting-only tapped target restriction, independent from resolution legality. |
| Chant of Vitu-Ghazi | Turn-long prevention from all creatures, with life gained from actual prevented damage. |
| Concerted Effort | Propagation of actual protection and landwalk variants as well as ordinary keywords. |
| Belltower Sphinx | Retain damage-source controller information for its self-damaged trigger. |
| Auratouched Mage, Flickerform | Legal attachment search, owner-controlled Aura return, and source-left fallback. |
| Eye of the Storm | Linked spell-card exile, copy choices controlled by the triggering caster, and repeated optional casts. |
| Spawnbroker | Existing ExchangeControl plus cross-target power filtering; prove both-target legality and exchange atomicity. |
| Sins of the Past | Targeted graveyard casting permission with duration and exile replacement, without moving the card prematurely. |
| Molten Sentry | Coin flip as an entry replacement with persistent stats and keyword. |
| Warp World | Per-owner counts including tokens, simultaneous entry batches, enchantments entering afterward, and bottom ordering. |
| Blood Funnel | Optional creature sacrifice and counter-on-nonpayment, composed with existing reduction and counter effects. |
| Shadow of Doubt | Search prohibition in the shared library-search path. |
| Szadek, Lord of Secrets | One damage replacement produces counters and mills the damaged player; independent replacements cannot both consume the same damage. |
| Crown of Convergence | Continuously compare creature colors with the current top library card. |
| Circu, Dimir Lobotomist | Name matching over the whole linked exile pile for casting restrictions. |
| Spectral Searchlight | Non-targeting player choice followed by that player's color choice, preserving mana-ability resolution. |
| Sunforger, Leashling | Unattach-equipment and hand-to-library costs, paid before resolution. |
| Brightflame | Aggregate actual damage dealt after prevention/replacement before life gain. |
| Master Warcraft | Attack/block declaration choice ownership across engine, server, and client. |
| Grifter’s Blade | Enter attached as a replacement, rather than an entry trigger. |
| Chorus of the Conclave | Optional arbitrary extra mana for creature spells, carried into entry counters. |
| Mindleech Mass | Existing hand selection and free-cast pipeline; prove timing and continuation behavior. |
| Gaze of the Gorgon | Combat-history tracking and delayed destruction alongside regeneration. |
