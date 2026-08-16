package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tidepool Turtle
 * {3}{U}
 * Creature — Turtle
 * 2/5
 * {2}{U}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val TidepoolTurtle = card("Tidepool Turtle") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Turtle"
    oracleText = "{2}{U}: Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"
    power = 2
    toughness = 5

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        effect = Effects.Scry(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Andrew Mar"
        flavorText = "Prized by wizards as a mobile scrying pool, it bears visions drawn from the depths of the sea."
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d397fb84-8573-4b2c-be2c-f8fd820342f0.jpg?1783921342"
    }
}
