package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Volatile Fjord
 *
 * Snow Land — Island Mountain
 * ({T}: Add {U} or {R}.)
 * This land enters tapped.
 *
 * One of Kaldheim's ten snow dual lands. The whole card is a single [EntersTapped] replacement
 * effect: the parenthesised mana line is reminder text for the intrinsic abilities the two basic
 * land subtypes already grant, so writing a mana ability here would let the land tap twice. The
 * Snow supertype rides along in the type line.
 */
val VolatileFjord = card("Volatile Fjord") {
    manaCost = ""
    colorIdentity = "RU"
    typeLine = "Snow Land — Island Mountain"
    oracleText = "({T}: Add {U} or {R}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land subtypes in the type line.

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "273"
        artist = "Randy Vargas"
        flavorText = "\"I watched with my own eyes as Jari Eagle-Caller fell from these cliffs, only to be snatched from the air by a giant bird!\"\n" +
            "—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2392fbb-d9c4-4688-b99c-4e7614c60c12.jpg"
    }
}
