package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Ice Tunnel
 *
 * Snow Land — Island Swamp
 * ({T}: Add {U} or {B}.)
 * This land enters tapped.
 *
 * One of Kaldheim's ten snow dual lands. The whole card is a single [EntersTapped] replacement
 * effect: the parenthesised mana line is reminder text for the intrinsic abilities the two basic
 * land subtypes already grant, so writing a mana ability here would let the land tap twice. The
 * Snow supertype rides along in the type line.
 */
val IceTunnel = card("Ice Tunnel") {
    manaCost = ""
    colorIdentity = "BU"
    typeLine = "Snow Land — Island Swamp"
    oracleText = "({T}: Add {U} or {B}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land subtypes in the type line.

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "262"
        artist = "Johannes Voss"
        flavorText = "\"The ice cracked underfoot; strange shapes swam beneath the surface. Who else has walked this blighted path?\"\n" +
            "—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8cff3ef0-4dfb-472e-aa1e-77613dd0f6d8.jpg"
    }
}
