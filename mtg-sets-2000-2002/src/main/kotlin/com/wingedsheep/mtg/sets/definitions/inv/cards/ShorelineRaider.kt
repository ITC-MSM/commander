package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Shoreline Raider
 * {2}{U}
 * Creature — Merfolk
 * 2/2
 * Protection from Kavu
 */
val ShorelineRaider = card("Shoreline Raider") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk"
    power = 2
    toughness = 2
    oracleText = "Protection from Kavu"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Subtype("Kavu")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Nelson DeCastro"
        flavorText = "\"This strange new beast makes for an excellent meal. Get me more.\"\n—Empress Galina"
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d895b3b8-2acc-4c9f-8341-f651c1255b7c.jpg?1562938535"
    }
}
