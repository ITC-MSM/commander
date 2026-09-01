package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Minotaur Aggressor
 * {6}{R}
 * Creature — Minotaur Berserker
 * 6/2
 *
 * First strike, haste
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Two evergreen keywords and nothing else.
 */
val MinotaurAggressor = card("Minotaur Aggressor") {
    manaCost = "{6}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Berserker"
    oracleText = "First strike, haste"
    power = 6
    toughness = 2

    keywords(Keyword.FIRST_STRIKE, Keyword.HASTE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "100"
        artist = "Lucas Graciano"
        flavorText = "The smelting district is home to many who see the guilds as not for them."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e22959dc-8759-454e-80b9-623a799af354.jpg?1783940354"
    }
}
