package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Winged Shepherd
 * {5}{W}
 * Creature — Angel
 * 3/3
 * Flying, vigilance
 * Cycling {W} ({W}, Discard this card: Draw a card.)
 */
val WingedShepherd = card("Winged Shepherd") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText = "Flying, vigilance\n" +
            "Cycling {W} ({W}, Discard this card: Draw a card.)"
    power = 3
    toughness = 3

    keywords(Keyword.FLYING, Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.cycling("{W}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Chris Rahn"
        flavorText = "\"When the Hour of Promise arrives, the God-Pharaoh will tear down the Hekma, for its protection will be needed no longer.\"\n—*The Accounting of Hours*"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04301470-82d9-43c7-aaf1-a41e185cb109.jpg?1783936530"
    }
}
