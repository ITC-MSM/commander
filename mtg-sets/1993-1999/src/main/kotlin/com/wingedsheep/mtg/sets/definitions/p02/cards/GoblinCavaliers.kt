package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Cavaliers
 * {2}{R}
 * Creature — Goblin
 * 3/2
 *
 * Vanilla — no rules text.
 */
val GoblinCavaliers = card("Goblin Cavaliers") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "DiTerlizzi"
        flavorText = "They get along so well with their goats because they're practically goats themselves."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edc6ea02-0642-4fc5-b50c-543dc393bfdd.jpg?1783946467"
    }
}
