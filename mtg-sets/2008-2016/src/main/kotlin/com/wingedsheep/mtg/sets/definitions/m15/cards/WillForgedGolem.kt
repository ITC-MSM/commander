package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Will-Forged Golem
 * {6}
 * Artifact Creature — Golem
 * 4/4
 * Convoke
 */
val WillForgedGolem = card("Will-Forged Golem") {
    manaCost = "{6}"
    typeLine = "Artifact Creature — Golem"
    power = 4
    toughness = 4
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)"

    keywords(Keyword.CONVOKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "239"
        artist = "Jason Felix"
        flavorText = "The modular nature of the automaton's design makes assembly perfectly intuitive."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/0175bafa-dc9f-464c-8f9e-dd4131732652.jpg?1783939153"
    }
}
