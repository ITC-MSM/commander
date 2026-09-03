package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Charity Extractor
 * {3}{B}
 * Creature — Human Knight
 * 1/5
 * Lifelink
 */
val CharityExtractor = card("Charity Extractor") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Knight"
    oracleText = "Lifelink"
    power = 1
    toughness = 5

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "Matt Stewart"
        flavorText = "\"War or no war, your donations are due, as always.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/5/3594f726-cdbb-4b7d-bcfe-17d5f8cd5228.jpg"
    }
}
