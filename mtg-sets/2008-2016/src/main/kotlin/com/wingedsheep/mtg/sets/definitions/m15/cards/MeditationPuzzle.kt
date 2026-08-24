package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Meditation Puzzle
 * {3}{W}{W}
 * Instant
 * Convoke
 * You gain 8 life.
 */
val MeditationPuzzle = card("Meditation Puzzle") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText =
        "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "You gain 8 life."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Effects.GainLife(8)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Mark Winters"
        flavorText = "Find your center, and you will find your way."
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f31f5c19-9650-4fdc-a316-866eddd29c1a.jpg?1783939200"
    }
}
