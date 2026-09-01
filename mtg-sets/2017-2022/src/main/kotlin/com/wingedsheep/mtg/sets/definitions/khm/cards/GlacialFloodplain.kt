package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Glacial Floodplain
 *
 * Snow Land — Plains Island
 * ({T}: Add {W} or {U}.)
 * This land enters tapped.
 *
 * One of Kaldheim's ten snow dual lands. The whole card is a single [EntersTapped] replacement
 * effect: the parenthesised mana line is reminder text for the intrinsic abilities the two basic
 * land subtypes already grant, so writing a mana ability here would let the land tap twice. The
 * Snow supertype rides along in the type line.
 */
val GlacialFloodplain = card("Glacial Floodplain") {
    manaCost = ""
    colorIdentity = "UW"
    typeLine = "Snow Land — Plains Island"
    oracleText = "({T}: Add {W} or {U}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land subtypes in the type line.

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "257"
        artist = "Sarah Finnigan"
        flavorText = "\"A cliff once rose from the surf here—until Bjora Dawn-Greeter declared that it was blocking her view and pulled it down bare-handed.\"\n" +
            "—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9de5fadd-4559-479f-b45d-abe792f0f6e5.jpg"
    }
}
