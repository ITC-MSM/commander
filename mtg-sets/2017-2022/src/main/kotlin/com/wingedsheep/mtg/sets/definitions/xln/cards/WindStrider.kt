package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wind Strider
 * {4}{U}
 * Creature — Merfolk Wizard
 * 3/3
 *
 * Flash
 * Flying
 */
val WindStrider = card("Wind Strider") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Flying"
    power = 3
    toughness = 3

    keywords(Keyword.FLASH, Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "Magali Villeneuve"
        flavorText = "\"Currents are currents, whether in sea or sky.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/1/613a8ab1-9c5e-4b56-aa51-daeb8c1983b9.jpg"
    }
}
