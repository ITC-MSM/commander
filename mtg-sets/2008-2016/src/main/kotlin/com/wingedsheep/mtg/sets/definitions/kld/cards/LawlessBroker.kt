package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lawless Broker
 * {2}{B}
 * Creature — Aetherborn Rogue
 * 3/2
 *
 * When this creature dies, put a +1/+1 counter on target creature you control.
 */
val LawlessBroker = card("Lawless Broker") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Aetherborn Rogue"
    oracleText = "When this creature dies, put a +1/+1 counter on target creature you control."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Dies
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
        description = "When this creature dies, put a +1/+1 counter on target creature you control."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Darek Zabrocki"
        flavorText = "Kaladesh's illicit marketplaces are known as \"night markets,\" but if you know who to ask, you can find what you're looking for at any time of the day."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b5bfff7-aa23-42ef-af1b-bc3304bd3a17.jpg?1783937205"
    }
}
