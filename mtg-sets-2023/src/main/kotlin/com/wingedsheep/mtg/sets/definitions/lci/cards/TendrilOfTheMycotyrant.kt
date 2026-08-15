package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Tendril of the Mycotyrant
 * {1}{G} — Creature — Fungus Wizard (Uncommon) — The Lost Caverns of Ixalan #215
 * Artist: Maxime Minard
 * 2/2
 *
 * {5}{G}{G}: Put seven +1/+1 counters on target noncreature land you control. It becomes
 * a 0/0 Fungus creature with haste. It's still a land.
 *
 * Modeled as an activated ability targeting a noncreature land you control:
 *   AddCountersEffect (seven +1/+1 counters) then BecomeCreatureEffect with base 0/0,
 *   Fungus, haste, Duration.Permanent. No "until end of turn" appears on the card — the
 *   land stays a creature unless it leaves the battlefield. The +1/+1 counters keep the
 *   effectively 0/0 body alive (0+7 = 7/7 while counters remain). It's still a land, so
 *   BecomeCreature adds the creature type/P-T without removing land types.
 *
 * Same "become a 0/0 creature with haste + N +1/+1 counters" animate shape as Cosmium
 * Confluence's Cave mode (LCI #181), reusing AddCounters + permanent BecomeCreature.
 */
val TendrilOfTheMycotyrant = card("Tendril of the Mycotyrant") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus Wizard"
    oracleText = "{5}{G}{G}: Put seven +1/+1 counters on target noncreature land you control. " +
        "It becomes a 0/0 Fungus creature with haste. It's still a land."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{5}{G}{G}")
        val land = target(
            "target noncreature land you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Land.notCreature().youControl()))
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 7, land) then
            Effects.BecomeCreature(
                target = land,
                power = 0,
                toughness = 0,
                keywords = setOf(Keyword.HASTE),
                creatureTypes = setOf("Fungus"),
                duration = Duration.Permanent
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Maxime Minard"
        flavorText = "Every mycoid carries within itself the makings of an entire colony."
        imageUri = "https://cards.scryfall.io/normal/front/a/f/afa464fe-978f-43de-ac35-79be4b12f0d9.jpg?1782694437"
    }
}
