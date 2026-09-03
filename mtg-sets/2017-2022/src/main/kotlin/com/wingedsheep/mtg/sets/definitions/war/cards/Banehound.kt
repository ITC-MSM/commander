package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Banehound
 * {B}
 * Creature — Nightmare Dog
 * 1/1
 * Lifelink, haste
 */
val Banehound = card("Banehound") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightmare Dog"
    oracleText = "Lifelink, haste"
    power = 1
    toughness = 1

    keywords(Keyword.LIFELINK, Keyword.HASTE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "YW Tang"
        flavorText = "\"I wish I could train a pack of them for hunting in the undercity. But I'd never dare turn my back, and I hate to think what I'd have to feed them.\"\n—Zhosmir, urban huntmaster"
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9e03567-c95a-40b8-a75a-971076093f57.jpg"
    }
}
