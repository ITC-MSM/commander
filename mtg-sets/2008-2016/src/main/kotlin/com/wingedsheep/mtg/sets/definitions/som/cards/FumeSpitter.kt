package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fume Spitter
 * {B}
 * Creature — Phyrexian Horror
 * 1/1
 *
 * Sacrifice this creature: Put a -1/-1 counter on target creature.
 */
val FumeSpitter = card("Fume Spitter") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Phyrexian Horror"
    power = 1
    toughness = 1
    oracleText = "Sacrifice this creature: Put a -1/-1 counter on target creature."

    activatedAbility {
        cost = Costs.SacrificeSelf
        val t = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, t)
        description = "Sacrifice this creature: Put a -1/-1 counter on target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Nils Hamm"
        flavorText = "\"Our archers made sport of it as it fumbled its way up the slag ridge. As it collapsed we thought ourselves safe, but the foul thing carried more than necrogen.\"\n—Adaran, Tangle hunter"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58cd149b-ecf4-43ed-b6e5-98870953b4b8.jpg?1783941731"
    }
}
