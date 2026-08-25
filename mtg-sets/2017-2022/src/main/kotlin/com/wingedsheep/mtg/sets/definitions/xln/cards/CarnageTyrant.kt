package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Carnage Tyrant
 * {4}{G}{G}
 * Creature — Dinosaur
 * 7/6
 *
 * This spell can't be countered.
 * Trample, hexproof
 */
val CarnageTyrant = card("Carnage Tyrant") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "This spell can't be countered.\n" +
        "Trample, hexproof"
    power = 7
    toughness = 6

    cantBeCountered = true

    keywords(Keyword.TRAMPLE, Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "179"
        artist = "Yeong-Hao Han"
        flavorText = "Sun Empire commanders are well versed in advanced martial strategy. Still, the correct maneuver is usually to deploy the giant, implacable death lizard."
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3bd78731-949c-464a-826a-92f86d784911.jpg"
    }
}
