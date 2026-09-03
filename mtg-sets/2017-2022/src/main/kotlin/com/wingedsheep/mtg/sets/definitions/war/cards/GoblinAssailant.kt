package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Assailant
 * {1}{R}
 * Creature — Goblin Warrior
 * 2/2
 *
 * Canonical relocated here from LTR: War of the Spark is the card's earliest real printing.
 */
val GoblinAssailant = card("Goblin Assailant") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Warrior"
    power = 2
    toughness = 2
    oracleText = ""

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Jesper Ejsing"
        flavorText = "What he lacks in patience, intelligence, empathy, lucidity, hygiene, ability to follow orders, self-regard, and discernible skills, he makes up for in sheer chaotic violence."
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e41acc81-7c22-4b59-97b8-54473623db6f.jpg"
    }
}
