package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Hero
 * {2}{R}
 * Creature — Goblin
 * 2/2
 *
 * Vanilla — no rules text.
 */
val GoblinHero = card("Goblin Hero") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Mark Tedin"
        flavorText = "They attacked in an orgy of rage and madness, but only one seemed as focused on killing us as on the sheer joy of battle."
        imageUri = "https://cards.scryfall.io/normal/front/7/1/7135a569-e5d3-4a1f-924b-bdb86926b4e1.jpg?1783947934"
    }
}
