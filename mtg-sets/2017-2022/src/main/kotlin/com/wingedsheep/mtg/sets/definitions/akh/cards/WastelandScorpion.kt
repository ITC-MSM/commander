package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Wasteland Scorpion
 * {2}{B}
 * Creature — Scorpion
 * 2/2
 * Deathtouch
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val WastelandScorpion = card("Wasteland Scorpion") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Scorpion"
    oracleText = "Deathtouch\n" +
            "Cycling {2} ({2}, Discard this card: Draw a card.)"
    power = 2
    toughness = 2

    keywords(Keyword.DEATHTOUCH)
    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Yeong-Hao Han"
        flavorText = "All but the gods fear the scorpion's sting."
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2bab1782-498c-40fc-bf2e-5c991d0c3501.jpg?1783936495"
    }
}
