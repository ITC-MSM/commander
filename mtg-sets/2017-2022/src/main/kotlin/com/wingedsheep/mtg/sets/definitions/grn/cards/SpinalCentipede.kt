package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Spinal Centipede
 * {2}{B}
 * Creature — Insect
 * 3/2
 * When this creature dies, put a +1/+1 counter on target creature you control.
 */
val SpinalCentipede = card("Spinal Centipede") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    oracleText = "When this creature dies, put a +1/+1 counter on target creature you control."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Dies
        val creature = target("target", Targets.CreatureYouControl)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Zezhou Chen"
        flavorText = "The Golgari adorn themselves with the exoskeletons of iridescent insects. It's only fair the insects do likewise."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74d579fb-615a-43a6-a1c1-c535087abf2a.jpg?1783934169"
    }
}
