package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Haunted Mire
 * Land — Swamp Forest
 * ({T}: Add {B} or {G}.)
 * This land enters tapped.
 *
 * Mana abilities are intrinsic from the two basic land types (the reminder line is the whole mana text).
 */
val HauntedMire = card("Haunted Mire") {
    colorIdentity = "BG"
    typeLine = "Land — Swamp Forest"
    oracleText = "({T}: Add {B} or {G}.)\nThis land enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "248"
        artist = "Bruce Brenneise"
        flavorText = "Some of the nature spirits and wraiths that haunt Urborg take offense at the term \"ghost town.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16f5e958-56e6-4666-a534-24deba6f652d.jpg?1783921260"
    }
}
