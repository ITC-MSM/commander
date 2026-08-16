package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Sacred Peaks
 * Land — Mountain Plains
 * ({T}: Add {R} or {W}.)
 * This land enters tapped.
 *
 * Mana abilities are intrinsic from the two basic land types (the reminder line is the whole mana text).
 */
val SacredPeaks = card("Sacred Peaks") {
    colorIdentity = "WR"
    typeLine = "Land — Mountain Plains"
    oracleText = "({T}: Add {R} or {W}.)\nThis land enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "254"
        artist = "Kamila Szutenberg"
        flavorText = "Everyone is welcome in the floating Serran cities, though few but the most faithful attempt the ascent."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24957d64-ad17-4d4f-8a00-3cdd83e1ce88.jpg?1783921260"
    }
}
