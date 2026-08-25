package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sky Terror
 * {R}{W}
 * Creature — Dinosaur
 * 2/2
 *
 * Flying, menace
 */
val SkyTerror = card("Sky Terror") {
    manaCost = "{R}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Dinosaur"
    oracleText = "Flying, menace"
    power = 2
    toughness = 2

    keywords(Keyword.FLYING, Keyword.MENACE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "229"
        artist = "Johann Bodin"
        flavorText = "\"Wherever the Threefold Sun shines, great wings may go.\"\n—Emperor Apatzec Intli III"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/167ed739-2953-47af-841f-bc1a092b3aa6.jpg"
    }
}
