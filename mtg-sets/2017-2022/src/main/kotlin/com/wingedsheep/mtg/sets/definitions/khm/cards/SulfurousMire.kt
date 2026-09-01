package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Sulfurous Mire
 *
 * Snow Land — Swamp Mountain
 * ({T}: Add {B} or {R}.)
 * This land enters tapped.
 *
 * One of Kaldheim's ten snow dual lands. The whole card is a single [EntersTapped] replacement
 * effect: the parenthesised mana line is reminder text for the intrinsic abilities the two basic
 * land subtypes already grant, so writing a mana ability here would let the land tap twice. The
 * Snow supertype rides along in the type line.
 */
val SulfurousMire = card("Sulfurous Mire") {
    manaCost = ""
    colorIdentity = "BR"
    typeLine = "Snow Land — Swamp Mountain"
    oracleText = "({T}: Add {B} or {R}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land subtypes in the type line.

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "270"
        artist = "Titus Lunter"
        flavorText = "\"In my youth, we used to best each other at hopping rabbit-fast between the lava spouts, the burning tar spattering our legs.\"\n" +
            "—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35ebe245-ebb5-493c-b9c1-56fbfda9bd66.jpg"
    }
}
