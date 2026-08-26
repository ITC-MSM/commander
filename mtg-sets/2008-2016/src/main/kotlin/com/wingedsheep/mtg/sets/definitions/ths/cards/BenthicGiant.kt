package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Benthic Giant
 * {5}{U}
 * Creature — Giant
 * 4 / 5
 *
 * Hexproof (This creature can't be the target of spells or abilities your opponents control.)
 */
val BenthicGiant = card("Benthic Giant") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Giant"
    power = 4
    toughness = 5
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"

    keywords(Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Jaime Jones"
        flavorText = "\"Some fates you can see coming for you, plain as day, and there's nothing you can do about them.\"\n—Callaphe the mariner"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/295f9eb0-5a75-4f86-aca9-3f76b0213c41.jpg"
    }
}
