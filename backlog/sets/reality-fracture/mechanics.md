# Reality Fracture (FRA) — Mechanics

Reviewed against all 285 unique cards in the Scryfall import on 2026-09-21, including prepare spell faces. This is an implementation map, not a claim that any FRA card is playable.

A checked box means the shared mechanic has existing SDK vocabulary and corpus examples. It does **not** certify every listed card’s full script. Unchecked sections identify shared missing behavior or combinations requiring focused engine verification before authoring those cards. Named and unnamed mechanics are ordered by card count; lists include all matches in this import, including reminder text and granted abilities.

Sources: [Scryfall FRA](https://scryfall.com/sets/fra), [SDK language reference](../../../docs/card-sdk-language-reference.md), SDK declarations and existing card definitions. Rule references were checked against the September 25, 2026 Comprehensive Rules linked by the [official rules page](https://magic.wizards.com/en/rules); that linked edition is dated after this review and includes FRA’s rules.

Scryfall currently marks all 461 printings as `booster: false`, yielding 0 draft cards and 285 extras. Preserve the tooling’s partition, but refresh the import when booster metadata changes. FRA has all five basic land names, so it needs no other set’s basic-land fallback. It is an expansion; leave sealed support at its normal default and retain `incomplete = true`.

### - [x] Surveil (43 cards)

Look at the top cards; put any in the graveyard and the rest back in any order (CR 701.25). Counts include the Jace token’s reminder-text ability.

**Engine support:** `Effects.Surveil` / `Patterns.Library.surveil`.

Cards: Academic Ascent; Arcane Amphisbaena; Avatar of Burgeoning Echoes; Campus Crier; Chandra, Chill of Compliance; Countersculpt; Diviner of Victory; Enlightened Confidant; Eye of Jace; Fatehold Chronologist; Hexhaven Battalion; Inspired Tethermage; Jace's Machinations; Keeper of the Quiet Hour; Mindseeker Oculus; No Admittance; Overwrite the Multiverse; Plan for All Outcomes; Proctor of Potential; Protege's Awakening; Prudent Fateseer; Refute Destiny; Repurposed Enforcer; Rewrite Regrets; Semester Foreseer; Solve for Disappointment; Surveillance Phantasm; Tam's Resistance; Theorist's Proxy; Violent Echoes; Vraska's Final Mercy; Way of the Cryomancer; Way of the Deathbringer; Way of the Healer; Way of the Mentor; Way of the Mind Sculptor; Way of the Necromancer; Way of the Paradox; Way of the Pyromancer; Way of the Warlord; Way of the Wildspeaker; Your Fate Ends Here; Yuriko, Hope from the Shadows

### - [x] Empower Jace (35 cards)

Choose a Jace planeswalker token you control, creating one with zero loyalty and the specified two loyalty abilities first if necessary, then add the specified loyalty counters (CR 701.71).

**Engine support:** `Patterns.Mechanic.empowerJace(n)` (fixed or `DynamicAmount`) over the predefined `Jace` planeswalker token; `EmpowerJaceScenarioTest` covers creation, reuse, the multi-token choice, nontoken Jaces, and empower Jace 0. "Planeswalkers you control have '[−N]: …'" is `GrantActivatedAbility(grantedLoyaltyAbility(-N) { … }, GroupFilter(Planeswalker.youControl()))`; granted loyalty abilities appear in the planeswalker's ability menu.

Cards: Academic Ascent; Arcane Amphisbaena; Avatar of Burgeoning Echoes; Campus Crier; Countersculpt; Fatehold Charm; Hexhaven Battalion; Inspired Tethermage; Jace's Machinations; Jace, Reality Sculptor; Keeper of the Quiet Hour; Mindseeker Oculus; No Admittance; Overwrite the Multiverse; Plan for All Outcomes; Protege's Awakening; Repurposed Enforcer; Rewrite Regrets; Sanctum Lurker; Solve for Disappointment; Tam's Resistance; Theorist's Proxy; Theorist's Sanctum; Violent Echoes; Vraska's Final Mercy; Way of the Cryomancer; Way of the Deathbringer; Way of the Healer; Way of the Mentor; Way of the Mind Sculptor; Way of the Necromancer; Way of the Paradox; Way of the Pyromancer; Way of the Warlord; Way of the Wildspeaker

### - [x] Flying (28 cards)

Can be blocked only by creatures with flying or reach (CR 702.9).

**Engine support:** `Keyword.FLYING`; existing corpus uses this keyword.

Cards: Aerid Konstrari; Archive Arbiter; Blossom-Blessed Angel; Curse-Marred Demon; Darklight Phoenix; Denzilore Fatehold; Desperate Futurescribe; Draconic Visitor; Emrakul, the Exigent Doom; Fatehold Chronologist; Geist of Saint Thalia; Ingris Stingerquill; Kwia Vigorbloom; Lyra, Archangel of Dawn; Lyra, Tolarian Archangel; Mind Meanderer; Rescue Girl, First Responder; Ruric Thar, Biomagus; Saheeli, Consul of Oversight; Screeching Soulbreaker; Shatterwing Pegasus; Sphinx of False Conclusions; Surveillance Phantasm; Tomik, Orzhov Lawmage; Traxos, Academy Guardian; Uldaros Theorix; Undulating Witness; Variable Chaser

### - [x] Prepared (24 cards)

A prepared permanent allows casting its linked prepare-spell copy, then becomes unprepared (CR 722). Scryfall’s tag also includes cards that prepare later; do not add an enters-prepared keyword to those.

**Engine support:** `prepare { }`, `Keyword.PREPARED`, `Effects.BecomePrepared` and `Effects.BecomeUnprepared`; existing SOS preparation cards demonstrate the lifecycle.

Cards: Bloodline Recollector; Blossom-Blessed Angel; Carnivorous Cultivator; Codie, Ravenous Codex; Diviner of Victory; Emergency Phytomedic; Fatehold Chronologist; Hallway Heckler; Heartwood Crafter; Hexhaven Dueling Arena; Infinite Coursework; Konstrari Improviser; Paradox Shaper; Pompous Battlemage; Prudent Fateseer; Pyre Rhymer; Semester Foreseer; Stingerquill Voxmancer; Theorix Metamage; Variable Chaser; Vigorbloom Vanguard; Void Extrapolator; Whiplash Wordsmith; Woodwork Prodigy

### - [x] Modal choices (18 cards)

Choose among printed modes; targets and choice timing belong to the owning spell or ability.

**Engine support:** `modal` / `Mode` and existing targeting flows; per-card mode restrictions still require review.

Cards: Archive Arbiter; Charge the Sanctum; Compel Brutality; Craftwork Crusher; Divining Duelist; Fatehold Charm; Fateshaper Aspirant; Fulminous Forte; Icy Reception; Konstrari Charm; Perfected Theory; Rise of the Deathbringer; Stingerquill Charm; Surgical Precision; Theorix Charm; Vigorbloom Charm; Vraska's Final Mercy; Yuriko, Hope from the Shadows

### - [x] Cadet tokens (13 cards)

Create 2/2 colorless Wizard Soldier creature tokens named Cadet.

**Engine support:** `Effects.CreateToken` / `TokenDefinition`; reuse the exact token definition and FRA token art.

Cards: Ajani Unrelenting; Command the Stage; Craftwork Crusher; Fatehold Chronologist; Germinate Recruits; Heartstring Puller; Hexhaven Battalion; Ingris Stingerquill; Prudent Fateseer; Recursive Recruitment; Semester Foreseer; Stingerquill Charm; Way of the Healer

### - [x] Planeswalker conditional dual lands (10 cards)

Dual lands enter tapped unless the printed planeswalker-control condition holds.

**Engine support:** Conditional enters-tapped replacement and projected permanent filters; match each printed threshold.

Cards: Dedicated Commons; Fatehold Annex; Formidable Commons; Innovative Commons; Konstrari Annex; Meticulous Commons; Stingerquill Annex; Theorix Annex; Transformative Commons; Vigorbloom Annex

### - [x] Trample (10 cards)

Allows excess combat damage to the defending player or permanent (CR 702.19).

**Engine support:** `Keyword.TRAMPLE`; existing corpus uses this keyword.

Cards: Arni, Renowned Champion; Craftwork Crusher; Curse-Marred Demon; Emrakul, the Exigent Doom; Frostbite Pyromental; Ghalta the Unstoppable; Heartstring Puller; Karn, Gilded Guardian; Ruric Thar, Magecrusher; Traxos, Scourge Eternal

### - [x] Flash (9 cards)

Allows casting whenever an instant could be cast (CR 702.8).

**Engine support:** `Keyword.FLASH`; existing corpus uses this keyword.

Cards: Denzilore Fatehold; Divining Duelist; Edgar, Moonlit Sovereign; Ferocity of the Hunt; Sphinx of False Conclusions; Teyo, Diamondblade Mage; Teyo, Lightshield Expert; Theorist's Proxy; Yuriko, Hope from the Shadows

### - [x] Prowess (9 cards)

Triggers +1/+1 until end of turn on each noncreature spell you cast (CR 702.108).

**Engine support:** `Keyword.PROWESS`; existing corpus uses this keyword.

Cards: Cryotheory Adept; Grim Repriser; Pompous Battlemage; Pyre Rhymer; Ruric Thar, Biomagus; Tetsuko Umezawa, Pursuer; Tomik, Izzet Sparkmage; Traxos, Academy Guardian; Variable Chaser

### - [x] Mill (8 cards)

Move the specified number of cards from the top of a library into its graveyard (CR 701.17).

**Engine support:** `Patterns.Library.mill` / library move pipelines.

Cards: Dark Matter Manipulator; Liliana the Repentant; Paradox Shaper; Primal Witchstalker; Something Worth Saving; Theorix Charm; Theorix Metamage; Void Extrapolator

### - [ ] Scry and surveil payoffs (8 cards)

Reward scry/surveil events or test whether either action occurred this turn.

**Engine support:** Event triggers exist as `Triggers.WheneverYouScryOrSurveil`. Turn-history conditions for Desperate Futurescribe, Proctor of Potential and Surveillance Phantasm need verification or implementation.

Cards: Denzilore Fatehold; Desperate Futurescribe; Diviner of Victory; Proctor of Potential; Proft, Consulting Detective; Prudent Fateseer; Saheeli, Consul of Oversight; Surveillance Phantasm

### - [x] Vigilance (8 cards)

Attacking does not cause tapping (CR 702.20).

**Engine support:** `Keyword.VIGILANCE`; existing corpus uses this keyword.

Cards: Blossom-Blessed Angel; Budding Insurgent; Hexhaven Invigorator; Karn, Gilded Guardian; Kwia Vigorbloom; Ruric Thar, Magecrusher; Surveillance Phantasm; Traxos, Academy Guardian

### - [x] Flashback (6 cards)

Cast from the graveyard for its flashback cost, then exile the spell when it leaves the stack (CR 702.34).

**Engine support:** `KeywordAbility.Flashback`; `Effects.GrantFlashback` covers Stingcaster Mage’s granted ability.

Cards: Bestial Incursion; Generous Revival; Predictive Preparations; Recursive Recruitment; Stingcaster Mage; Twinned Vision

### - [x] Haste (6 cards)

Allows attacking and tap-symbol abilities without waiting a turn (CR 702.10).

**Engine support:** `Keyword.HASTE`; existing corpus uses this keyword.

Cards: Chandra's Emberling; Craterclaw Colossus; Darklight Phoenix; Frostbite Pyromental; Gallia, the Merrymaker; Stingcaster Mage

### - [x] Heartwood tokens (6 cards)

Create red and green artifact tokens with a tap ability producing red or green mana.

**Engine support:** `Effects.CreateHeartwood(count?, tapped?, controller?)` over the predefined `Heartwood` token (red and green `Artifact — Heartwood`, `{T}: Add {R} or {G}`). Heartwood Crafter’s restricted mana (“can’t be spent to cast spells from your hand”) is a separate investigation.

Cards: Aerid Konstrari; Heartwood Crafter; Hungering Puppetbeast; Konstrari Improviser; Tenured Tethermage; Woodwork Prodigy

### - [ ] Loyalty activation interactions (6 cards)

Trigger on, modify timing of, or inspect activation of loyalty abilities.

**Engine support:** Generic ability-activation events and granted activated abilities exist. Loyalty-specific event filtering, counters spent, history and restricted instant-speed permissions are not covered by a generic planeswalker source filter.

Cards: Ajani Unrelenting; Gideon the Oathless; Jace's Machinations; Kiora of Salt and Sand; Way of the Mind Sculptor; Way of the Paradox

### - [x] Menace (6 cards)

Requires at least two blockers (CR 702.111).

**Engine support:** `Keyword.MENACE`; existing corpus uses this keyword.

Cards: Apex Witchstalker; Gallia, Tragic Host; Jiang Yanggu, Alone; Master of Barbs; Primal Witchstalker; Proft, Sinister Mastermind

### - [ ] Noncombat damage payoffs (6 cards)

Reward, replace, or remember noncombat damage; several cards test damage this turn or last turn.

**Engine support:** Damage events/replacements exist. Generic opponent noncombat-damage turn history and grouped one-or-more-opponents triggers require focused engine review.

Cards: Command the Stage; Grim Repriser; Massacre Girl, Most Wanted; Master of Barbs; Tomik, Izzet Sparkmage; Whiplash Wordsmith

### - [x] Reach (6 cards)

Allows blocking flying creatures (CR 702.17).

**Engine support:** `Keyword.REACH`; existing corpus uses this keyword.

Cards: Koth, the Geomancer; Ruric Thar, Magecrusher; Sureshot Sower; Tether Technician; Titanbones, Towering Heart; Vinelasher Adept

### - [x] Basic landcycling (5 cards)

Pay the cycling cost and discard this card to search for a basic land, reveal it, put it in hand, then shuffle (CR 702.29). The separate Cycling, Landcycling and Typecycling tags describe these same five cards.

**Engine support:** `KeywordAbility.basicLandcycling`, already used by Kulrath Zealot and Stratosoarer.

Cards: Apex Witchstalker; Awaken the Inferno; Hexhaven Battalion; Undulating Witness; Vinelasher Adept

### - [x] Deathtouch (5 cards)

Makes any positive damage dealt to a creature lethal (CR 702.2).

**Engine support:** `Keyword.DEATHTOUCH`; existing corpus uses this keyword.

Cards: Arcane Amphisbaena; Carnivorous Cultivator; Mabel, Bitter Recluse; Rampart Hunter; Vraska, the Cutting Glare

### - [x] Threshold (5 cards)

Checks for at least seven cards in your graveyard. Threshold is an ability word, with no independent rules meaning (CR 207.2c).

**Engine support:** `Conditions.CardsInGraveyardAtLeast(7)`; see Nimble Mongoose. Card-specific effects still need review.

Cards: Loot, the Anomaly; Null Summoner; Proft, Sinister Mastermind; Theorix Metamage; Void Extrapolator

### - [x] Ward (5 cards)

Counters an opponent’s targeting spell or ability unless its controller pays the ward cost (CR 702.21).

**Engine support:** `KeywordAbility.Ward` and the ward additional-cost model; select the exact printed cost.

Cards: Emrakul, the Exigent Doom; Gideon the Oathless; Kwia Vigorbloom; Unflinching Hortimancer; Wrecking Gecko

### - [x] Equip (4 cards)

A sorcery-speed activated ability attaches Equipment to a creature you control (CR 702.6).

**Engine support:** `equipAbility` / `ActivatedAbility.equip`. Warrior’s Blades additionally needs target-dependent equip cost reduction; see focused investigations.

Cards: Hunter's Axe; Lich's Relic; Medic's Kitesail; Warrior's Blades

### - [x] Lifelink (4 cards)

Damage also gains that much life for the source’s controller (CR 702.15).

**Engine support:** `Keyword.LIFELINK`; existing corpus uses this keyword.

Cards: Blessed Ghoul; Enlightened Confidant; Kwia Vigorbloom; Thalia, the Survivor

### - [x] Enchant (3 cards)

Restricts what an Aura may enchant (CR 702.5).

**Engine support:** `auraTarget` and target filters.

Cards: Ferocity of the Hunt; Infinite Coursework; Puppet Crafting

### - [x] Fight (3 cards)

Two creatures each deal damage equal to their power to the other (CR 701.14).

**Engine support:** `Effects.Fight`.

Cards: Mind Meanderer; Vigorbloom Charm; Yoshimaru, Scrappy Stray

### - [x] Landfall (3 cards)

Triggers when a land enters under your control; an ability word rather than a separate rules action (CR 207.2c).

**Engine support:** `Triggers.LandYouControlEnters` and ordinary land-entry triggers.

Cards: Avatar of Burgeoning Echoes; Koth of the Homestead; Koth, the Geomancer

### - [x] Behold (2 cards)

Reveal a matching card from hand or choose a matching permanent you control (CR 701.4).

**Engine support:** `Effects.Behold` and `Costs.additional.BeholdOrPay`. Theorist’s Sanctum’s as-enters choice still needs replacement-path review.

Cards: Countersculpt; Theorist's Sanctum

### - [x] First strike (2 cards)

Deals combat damage in the first combat damage step (CR 702.7).

**Engine support:** `Keyword.FIRST_STRIKE`; existing corpus uses this keyword.

Cards: Danitha, Spear of Agony; Danitha, Sword of Hope

### - [x] Treasure (2 cards)

An artifact token with a tap-and-sacrifice ability producing one mana of any color (CR 111.10a).

**Engine support:** `Effects.CreateToken` and token mana abilities. Vraska, Soul of Stone needs its additional creature characteristics.

Cards: Vraska, Soul of Stone; Vraska, the Cutting Glare

### - [x] Convoke (1 card)

Allows creatures to tap to help pay the spell’s mana cost (CR 702.51).

**Engine support:** `Keyword.CONVOKE`; existing corpus uses this keyword.

Cards: Winter, Team Player

### - [x] Defender (1 card)

Prevents attacking unless another effect allows it (CR 702.3).

**Engine support:** `Keyword.DEFENDER`; existing corpus uses this keyword.

Cards: Surveillance Phantasm

### - [x] Domain (1 card)

Counts basic land types among lands you control; an ability word (CR 207.2c).

**Engine support:** `DynamicAmounts.domain`; inspect Fblthp’s exact multiplier when authoring.

Cards: Fblthp, Knows the Way

### - [x] Double strike (1 card)

Deals damage in both combat damage steps (CR 702.4).

**Engine support:** `Keyword.DOUBLE_STRIKE`; existing corpus uses this keyword.

Cards: Tetsuko Umezawa, Pursuer

### - [x] Exhaust (1 card)

An activated ability usable only once for that object unless another effect permits reuse (CR 702.177).

**Engine support:** Activated ability `isExhaust` flag and once-only activation tracking, used by DFT cards.

Cards: Liliana the Repentant

### - [x] Proliferate (1 card)

Choose any number of players and permanents with counters and add another of every kind already there (CR 701.34).

**Engine support:** `Effects.Proliferate`. Tam’s repetition count needs distinct planeswalker-subtype aggregation; see investigations.

Cards: Tam, the Possibility

## Focused card investigations

These are review targets, not declarations that new effect types are necessary. Prefer existing compositions and add behavioral tests for any new engine vocabulary.

- **Samut, Tyrant of Naktamun:** split second and granting it to controlled instant/sorcery spells are absent from the keyword vocabulary.
- **Sanctum Lurker:** done — `GrantKeyword(AbilityFlag.SURVIVES_ZERO_LOYALTY.name, …)` exempts planeswalkers from CR 704.5i; the +2 is a `grantedLoyaltyAbility`.
- **Loot, the Anomaly:** treating negative power as positive for combat damage. **Loot, the Nexus:** distinct-power aggregation already exists; use it rather than adding another amount type.
- **Ruric Thar, Magecrusher:** lifetime history of combat damage, including damage to nonplayers. A damage-to-player predicate is insufficient.
- **Ruric Thar, Biomagus:** two independent prowess instances must survive representation and both trigger.
- **Inspired Tethermage:** count loyalty counters placed, with correct event grouping. **Jace, Reality Sculptor:** total loyalty of filtered permanents as an activation gate.
- **Tam, the Possibility:** distinct projected planeswalker types determine how often to proliferate.
- **Heartwood Crafter:** mana restriction excludes spells cast from hand, rather than excluding spells generally.
- **Warrior’s Blades:** equip reduction depends on counters on the chosen equip target.
- **Tomik, Orzhov Lawmage:** attacker cap per planeswalker. **Yuriko, Blade of the Mighty:** combat-wide spell and nonmana activation restrictions.
- **Uldaros Theorix:** distinct card types across selected targets and copied-card casting under a shared mana-value budget.
- **Violent Echoes:** carry numerical excess damage to the subsequent empower instruction.
- **Cruel Calculations:** number of cards each player milled this turn. **Draconic Visitor:** replace tokens with the prescribed token template.
- **Emrakul, the Exigent Doom:** duration tied to casting the specific exiled card. **Hall of Echoes:** temporary legend-rule exception.
- **Lich’s Relic** and **Primal Witchstalker:** reflexive triggers select their targets after the preceding choice, not on the original enters trigger.
- **Omnipresence:** dynamically restricted free casting; check the complete permission path.

## Suggested implementation order

1. FRA now has all five basic land types (25 artwork variants) and the five allied slowland reprints (10 printing records). Add the remaining reprints using their earliest canonical definitions. Scryfall still marks these basic-land variants outside boosters, so their limited-deckbuilding availability remains disabled pending corrected metadata.
2. Run `just assay-ready FRA` to identify cards that compose existing vocabulary. Implement and verify those in small related batches.
3. ~~Build Empower Jace~~ — done (`Patterns.Mechanic.empowerJace`).
4. Extend loyalty activation events/history and permissions, then author their payoffs. Address the focused investigations in separate mechanic-sized changes.

Use current Scryfall data for canonical printing placement, metadata and rulings when implementing each card. Neither this map nor a checked mechanic replaces the Assay differential gate or behavioral tests.
