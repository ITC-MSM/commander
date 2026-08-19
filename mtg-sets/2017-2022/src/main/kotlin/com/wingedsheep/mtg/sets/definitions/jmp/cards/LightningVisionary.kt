package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lightning Visionary
 * {1}{R}
 * Creature — Minotaur Shaman
 * 2/1
 *
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 */
val LightningVisionary = card("Lightning Visionary") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Shaman"
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"
    power = 2
    toughness = 1

    keywords(Keyword.PROWESS)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Bryan Sola"
        flavorText = "\"The gods don't speak to mortals with gentle entreaties. They speak with thunder and fury.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af648aaf-a8e0-4291-acf9-5f8533728f92.jpg?1783930502"
    }
}
