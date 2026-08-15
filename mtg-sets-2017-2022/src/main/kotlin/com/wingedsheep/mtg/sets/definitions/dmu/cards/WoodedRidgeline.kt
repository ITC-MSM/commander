package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Wooded Ridgeline
 *
 * Land — Mountain Forest
 * ({T}: Add {R} or {G}.)
 * This land enters tapped.
 */
val WoodedRidgeline = card("Wooded Ridgeline") {
    colorIdentity = "RG"
    typeLine = "Land — Mountain Forest"
    oracleText = "({T}: Add {R} or {G}.)\nThis land enters tapped."

    // Mana abilities are intrinsic from basic land types (Mountain → {R}, Forest → {G})

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "260"
        artist = "Jinho Bae"
        flavorText = "The power of long-forgotten empires still pulses in the earth of Dominaria, giving rise to strange, mana-rich pockets of growth."
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8e31184-dca4-48b1-be9d-581247c41d99.jpg?1673308403"
    }
}
