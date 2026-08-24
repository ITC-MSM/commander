package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Veiled Shade
 * {2}{B}
 * Creature — Shade
 * 2/2
 * {1}{B}: This creature gets +1/+1 until end of turn.
 */
val VeiledShade = card("Veiled Shade") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade"
    oracleText = "{1}{B}: This creature gets +1/+1 until end of turn."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "Anna Steinbauer"
        flavorText = "\"I sang songs of sorrow for my lost love. Imagine my horror when, one night, they were answered.\"\n—Milana, Orzhov prelate"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35cb18ae-0229-40a1-8838-ffb678ab2ed9.jpg?1783934169"
    }
}
