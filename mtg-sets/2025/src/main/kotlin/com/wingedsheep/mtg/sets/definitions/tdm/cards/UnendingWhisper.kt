package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Unending Whisper — Tarkir: Dragonstorm #62
 * {U} · Sorcery
 *
 * Draw a card.
 * Harmonize {5}{U} (You may cast this card from your graveyard for its harmonize cost.
 * You may tap a creature you control to reduce that cost by {X}, where X is its power.
 * Then exile this spell.)
 */
val UnendingWhisper = card("Unending Whisper") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw a card.\n" +
        "Harmonize {5}{U} (You may cast this card from your graveyard for its harmonize cost. " +
        "You may tap a creature you control to reduce that cost by {X}, where X is its power. Then exile this spell.)"

    spell {
        effect = Effects.DrawCards(1)
    }

    keywordAbility(KeywordAbility.harmonize("{5}{U}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Danny Schwartz"
        flavorText = "In the Endless Song, Temur's whisperers hear spirits murmur of the past, present, and future."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc48180a-ccac-469f-938d-c050821d0160.jpg?1743204207"
    }
}
