package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shepherd of the Lost
 * {4}{W}
 * Creature — Angel
 * 3/3
 * Flying, first strike, vigilance
 */
val ShepherdOfTheLost = card("Shepherd of the Lost") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 3
    toughness = 3
    oracleText = "Flying, first strike, vigilance"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "Kekai Kotaki"
        flavorText = "\"Should you fall in the wilds, lift your voice to the Sky Realm. The one who answers will be your salvation.\"\n—Emeria's Creed"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff23b1c2-7b99-4504-8944-ada264725524.jpg"
    }
}
