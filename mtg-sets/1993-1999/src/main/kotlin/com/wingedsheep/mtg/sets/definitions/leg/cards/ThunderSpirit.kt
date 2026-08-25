package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thunder Spirit
 * {1}{W}{W}
 * Creature — Elemental Spirit
 * 2/2
 *
 * Flying, first strike
 */
val ThunderSpirit = card("Thunder Spirit") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elemental Spirit"
    power = 2
    toughness = 2
    oracleText = "Flying, first strike"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "39"
        artist = "Randy Asplund-Faith"
        flavorText = "\"It was full of fire and smoke and light and . . . it drove between us and the Efrafans " +
            "like a thousand thunderstorms with lightning.\"\n" +
            "—Richard Adams, *Watership Down*"
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61a59775-b1cd-4ed0-8abf-c2b37f7be0d5.jpg?1783948079"
    }
}
