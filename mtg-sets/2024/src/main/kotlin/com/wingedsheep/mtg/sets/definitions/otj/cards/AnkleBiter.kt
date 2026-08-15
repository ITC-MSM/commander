package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ankle Biter
 * {G}
 * Creature — Snake
 * 1/1
 * Deathtouch
 */
val AnkleBiter = card("Ankle Biter") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Snake"
    power = 1
    toughness = 1
    oracleText = "Deathtouch"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Monztre"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/424972d6-3b2c-449b-b786-749a77020fa1.jpg?1712355878"
        flavorText = "\"Little tip, newcomer. Give your boots a shake in the morning before you put them on.\"\n—Annie Flash, to Kellan"
    }
}
