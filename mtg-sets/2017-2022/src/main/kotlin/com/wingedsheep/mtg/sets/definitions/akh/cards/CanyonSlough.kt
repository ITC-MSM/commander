package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Canyon Slough
 *
 * Land — Swamp Mountain
 * ({T}: Add {B} or {R}.)
 * This land enters tapped.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val CanyonSlough = card("Canyon Slough") {
    colorIdentity = "BR"
    typeLine = "Land — Swamp Mountain"
    oracleText = "({T}: Add {B} or {R}.)\n" +
        "This land enters tapped.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    replacementEffect(EntersTapped())

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "239"
        artist = "Titus Lunter"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8cb273d9-466d-416d-b27d-d1bc8a249076.jpg?1783936446"
    }
}
