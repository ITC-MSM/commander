package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Woodland Chasm
 *
 * Snow Land — Swamp Forest
 * ({T}: Add {B} or {G}.)
 * This land enters tapped.
 *
 * One of Kaldheim's ten snow dual lands. The whole card is a single [EntersTapped] replacement
 * effect: the parenthesised mana line is reminder text for the intrinsic abilities the two basic
 * land subtypes already grant, so writing a mana ability here would let the land tap twice. The
 * Snow supertype rides along in the type line.
 */
val WoodlandChasm = card("Woodland Chasm") {
    manaCost = ""
    colorIdentity = "BG"
    typeLine = "Snow Land — Swamp Forest"
    oracleText = "({T}: Add {B} or {G}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land subtypes in the type line.

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "274"
        artist = "Titus Lunter"
        flavorText = "\"Is this the grave of a god? A tunnel carved by the Cosmos Serpent? It matters not. It is no place for us.\"\n" +
            "—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2dd0b71-5a60-418c-82fc-f13d1b5075d0.jpg"
    }
}
