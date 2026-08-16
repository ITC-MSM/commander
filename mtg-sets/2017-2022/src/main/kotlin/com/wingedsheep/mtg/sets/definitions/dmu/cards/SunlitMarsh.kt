package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Sunlit Marsh
 * Land — Plains Swamp
 * ({T}: Add {W} or {B}.)
 * This land enters tapped.
 *
 * Mana abilities are intrinsic from the two basic land types (the reminder line is the whole mana text).
 */
val SunlitMarsh = card("Sunlit Marsh") {
    colorIdentity = "WB"
    typeLine = "Land — Plains Swamp"
    oracleText = "({T}: Add {W} or {B}.)\nThis land enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "257"
        artist = "Marc Simonetti"
        flavorText = "Even the poisoned land around the Stronghold blossomed with new life in the wake of the Great Mending."
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0588fbf-3c05-452a-b3f7-67604c1f921d.jpg?1783921257"
    }
}
