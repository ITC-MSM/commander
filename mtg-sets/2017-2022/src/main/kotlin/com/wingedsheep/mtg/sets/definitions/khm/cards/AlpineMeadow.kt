package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Alpine Meadow
 *
 * Snow Land — Mountain Plains
 * ({T}: Add {R} or {W}.)
 * This land enters tapped.
 *
 * One of Kaldheim's ten snow dual lands. The whole card is a single [EntersTapped] replacement
 * effect: the parenthesised mana line is reminder text for the intrinsic abilities the two basic
 * land subtypes already grant, so writing a mana ability here would let the land tap twice. The
 * Snow supertype rides along in the type line.
 */
val AlpineMeadow = card("Alpine Meadow") {
    manaCost = ""
    colorIdentity = "RW"
    typeLine = "Snow Land — Mountain Plains"
    oracleText = "({T}: Add {R} or {W}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land subtypes in the type line.

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "248"
        artist = "Piotr Dura"
        flavorText = "\"Here perished Rognar the Reckless after his hundred-day battle with the Ironmaw Dragon. We raised these stones to mark his resting place.\"\n" +
            "—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/8/7/8702d6b9-bb01-4841-a76d-4a576066c772.jpg"
    }
}
