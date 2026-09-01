package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Rimewood Falls
 *
 * Snow Land — Forest Island
 * ({T}: Add {G} or {U}.)
 * This land enters tapped.
 *
 * One of Kaldheim's ten snow dual lands. The whole card is a single [EntersTapped] replacement
 * effect: the parenthesised mana line is reminder text for the intrinsic abilities the two basic
 * land subtypes already grant, so writing a mana ability here would let the land tap twice. The
 * Snow supertype rides along in the type line.
 */
val RimewoodFalls = card("Rimewood Falls") {
    manaCost = ""
    colorIdentity = "GU"
    typeLine = "Snow Land — Forest Island"
    oracleText = "({T}: Add {G} or {U}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land subtypes in the type line.

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "266"
        artist = "Piotr Dura"
        flavorText = "\"For a fortnight, our warband lay still beneath the icy waters, breathing through reeds as we waited for the great bear to appear.\"\n" +
            "—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da1db084-f235-4e26-8867-5f0835a0d283.jpg"
    }
}
