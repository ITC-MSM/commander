package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fevered Convulsions
 * {B}{B}
 * Enchantment
 * {2}{B}{B}: Put a -1/-1 counter on target creature.
 */
val FeveredConvulsions = card("Fevered Convulsions") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "{2}{B}{B}: Put a -1/-1 counter on target creature."

    activatedAbility {
        cost = Costs.Mana("{2}{B}{B}")
        val t = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "136"
        artist = "Jeff Miracola"
        flavorText = "\"Tell me again why you failed to capture Gerrard, you worthless pile of spine.\"\n" +
            "—Volrath, to Greven"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a790769-e76e-49e9-9d6d-05ce8e858243.jpg"
    }
}
