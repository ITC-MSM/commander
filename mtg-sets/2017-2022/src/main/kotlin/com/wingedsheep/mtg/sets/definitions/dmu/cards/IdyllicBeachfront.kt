package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Idyllic Beachfront
 * Land — Plains Island
 * ({T}: Add {W} or {U}.)
 * This land enters tapped.
 *
 * Mana abilities are intrinsic from the two basic land types (the reminder line is the whole mana text).
 */
val IdyllicBeachfront = card("Idyllic Beachfront") {
    colorIdentity = "WU"
    typeLine = "Land — Plains Island"
    oracleText = "({T}: Add {W} or {U}.)\nThis land enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Robin Olausson"
        flavorText = "In the tradition of the original Tolarian Academy, every Tolarian campus is built near a body of water."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c50ec22c-decb-419f-ae52-78ea1706eb11.jpg?1783921261"
    }
}
