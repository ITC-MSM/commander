package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wayward Giant
 * {4}{R}
 * Creature — Giant
 * 4 / 5
 *
 * Menace
 */
val WaywardGiant = card("Wayward Giant") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    oracleText = "Menace"
    power = 4
    toughness = 5

    keywords(Keyword.MENACE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Filip Burburan"
        flavorText = "\"The giants follow the flow of aether, and twice a year it leads them through here. Lesson learned. More clearance is needed.\"\n—Sram, senior edificer"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db01e574-7a96-472c-8e5a-bbd503280c71.jpg?1783937185"
    }
}
