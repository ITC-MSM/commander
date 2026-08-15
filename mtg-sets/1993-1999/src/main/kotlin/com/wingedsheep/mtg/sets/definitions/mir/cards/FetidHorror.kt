package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fetid Horror
 * {3}{B}
 * Creature — Shade Horror
 * 1/2
 * {B}: This creature gets +1/+1 until end of turn.
 */
val FetidHorror = card("Fetid Horror") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade Horror"
    power = 1
    toughness = 2
    oracleText = "{B}: This creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Gary Leach"
        flavorText = "\"Of the six who went down the Uuserk Trail to scout ahead, one returned. She clawed at her eyes and nostrils and sobbed with horror. I was curious about what she saw, but we chose another path.\"\n—Scout Ekemet, final journal"
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4be39d50-1e36-4dac-a923-81fc9f229b8d.jpg?1572636951"
    }
}
