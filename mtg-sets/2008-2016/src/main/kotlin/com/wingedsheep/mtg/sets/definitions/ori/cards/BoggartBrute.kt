package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Boggart Brute
 * {2}{R}
 * Creature — Goblin Warrior
 * 3/2
 *
 * Menace (This creature can't be blocked except by two or more creatures.)
 */
val BoggartBrute = card("Boggart Brute") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)"
    power = 3
    toughness = 2

    keywords(Keyword.MENACE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "133"
        artist = "Igor Kieryluk"
        flavorText = "He has the biggest bashing stick, so it's a safe bet he's the leader."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d735ebf-61a4-4507-9399-6d32c8903ded.jpg?1783938333"
    }
}
