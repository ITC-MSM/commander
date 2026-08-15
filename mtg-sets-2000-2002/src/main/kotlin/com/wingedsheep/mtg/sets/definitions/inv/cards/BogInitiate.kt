package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bog Initiate
 * {1}{B}
 * Creature — Human Wizard
 * 1/1
 *
 * {1}: Add {B}.
 */
val BogInitiate = card("Bog Initiate") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "{1}: Add {B}."

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "rk post"
        imageUri = "https://cards.scryfall.io/normal/front/8/9/8962dc3b-24ca-4c3c-ba1d-933c29cf7b73.jpg?1562922788"
    }
}
