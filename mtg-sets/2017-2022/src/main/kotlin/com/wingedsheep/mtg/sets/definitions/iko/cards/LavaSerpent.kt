package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Lava Serpent
 * {5}{R}
 * Creature — Elemental Serpent
 * 5/5
 * Haste
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val LavaSerpent = card("Lava Serpent") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Serpent"
    power = 5
    toughness = 5
    oracleText = "Haste\nCycling {2} ({2}, Discard this card: Draw a card.)"

    keywords(Keyword.HASTE)

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "124"
        artist = "Jason A. Engle"
        flavorText = "Lavabrink's boiling moat is a deterrent for most, and a relaxing bath for one."
        imageUri = "https://cards.scryfall.io/normal/front/0/0/00ebd57f-7f7c-41b0-aa56-511c1816bc14.jpg?1783931047"
    }
}
