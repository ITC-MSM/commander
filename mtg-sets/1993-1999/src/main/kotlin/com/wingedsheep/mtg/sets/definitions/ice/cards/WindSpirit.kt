package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wind Spirit
 * {4}{U}
 * Creature — Elemental Spirit
 * 3/2
 *
 * Flying
 * Menace (This creature can't be blocked except by two or more creatures.)
 *
 * Flying plus menace. Menace is the modern Oracle wording of the printed "can't be blocked
 * except by two or more creatures"; `BlockPhaseManager` enforces it directly.
 */
val WindSpirit = card("Wind Spirit") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Spirit"
    power = 3
    toughness = 2
    oracleText = "Flying\n" +
        "Menace (This creature can't be blocked except by two or more creatures.)"

    keywords(Keyword.FLYING, Keyword.MENACE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "106"
        artist = "Kaja Foglio"
        flavorText = "\"To visit the sky requires bravery, and thought, and little else. To master the sky requires the binding of *its* masters, and little else.\"\n—Arnjlot Olasson, Sky Mage"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d882447-9594-4aab-b1a7-8bb275f250cf.jpg"
    }
}
