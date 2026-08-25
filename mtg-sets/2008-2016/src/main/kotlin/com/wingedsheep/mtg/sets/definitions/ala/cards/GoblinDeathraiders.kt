package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Deathraiders
 * {B}{R}
 * Creature — Goblin Warrior
 * 3 / 1
 * Trample
 *
 * A vanilla body with one evergreen keyword — [Keyword.TRAMPLE] via `keywords(...)`, which the engine
 * reads directly in combat damage assignment. No script at all.
 */
val GoblinDeathraiders = card("Goblin Deathraiders") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Goblin Warrior"
    power = 3
    toughness = 1
    oracleText = "Trample"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Raymond Swanland"
        flavorText = "Every once in a while, when they aren't getting incinerated in lava, crushed under rock slides, or devoured by dragons, goblins experience moments of unmitigated glory in battle."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76fd1253-1af1-42a7-9875-4d6ac9ce722c.jpg"
    }
}
