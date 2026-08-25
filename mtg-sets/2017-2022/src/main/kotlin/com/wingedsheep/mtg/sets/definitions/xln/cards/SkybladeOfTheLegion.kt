package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Skyblade of the Legion
 * {1}{W}
 * Creature — Vampire Soldier
 * 1/3
 *
 * Flying
 */
val SkybladeOfTheLegion = card("Skyblade of the Legion") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Soldier"
    oracleText = "Flying"
    power = 1
    toughness = 3

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Daarken"
        flavorText = "Vampires call the gift of flight \"exultation.\" For their enemies, it brings only sorrow."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67e788e2-12e9-4041-8210-753aaef2576c.jpg"
    }
}
