package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Arctic Treeline
 *
 * Snow Land — Forest Plains
 * ({T}: Add {G} or {W}.)
 * This land enters tapped.
 *
 * One of Kaldheim's ten snow dual lands. The whole card is a single [EntersTapped] replacement
 * effect: the parenthesised mana line is reminder text for the intrinsic abilities the two basic
 * land subtypes already grant, so writing a mana ability here would let the land tap twice. The
 * Snow supertype rides along in the type line.
 */
val ArcticTreeline = card("Arctic Treeline") {
    manaCost = ""
    colorIdentity = "GW"
    typeLine = "Snow Land — Forest Plains"
    oracleText = "({T}: Add {G} or {W}.)\n" +
        "This land enters tapped."

    // Mana abilities are intrinsic from the basic land subtypes in the type line.

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Alayna Danner"
        flavorText = "\"When the Light of Starnheim shines here, every frost-edged needle glitters with the reflected glory of the Cosmos.\"\n" +
            "—Iskene, Kannah storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b20e3117-f1e4-4449-ae9d-0b66abfc717d.jpg"
    }
}
