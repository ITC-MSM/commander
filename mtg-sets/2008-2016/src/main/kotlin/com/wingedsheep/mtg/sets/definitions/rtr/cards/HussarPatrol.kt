package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hussar Patrol
 * {2}{W}{U}
 * Creature — Human Knight
 * 2/4
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * Vigilance
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Two evergreen keywords and nothing else.
 */
val HussarPatrol = card("Hussar Patrol") {
    manaCost = "{2}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Human Knight"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Vigilance"
    power = 2
    toughness = 4

    keywords(Keyword.FLASH, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Seb McKinnon"
        flavorText = "\"You think no one is watching, you think you're smart enough to escape, and most foolish of all, you think no one cares.\"\n" +
            "—Arrester Lavinia, Tenth Precinct"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/dd775231-e1e0-41e2-ad9a-0726624f57f9.jpg?1783940338"
    }
}
