package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Goblin Roughrider
 * {2}{R}
 * Creature — Goblin Knight
 * 3/2
 *
 * Vanilla — no rules text.
 */
val GoblinRoughrider = card("Goblin Roughrider") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Knight"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Jesper Ejsing"
        flavorText = "Astride the bucking creature, Gribble hurtled down the mountainside while his Grotag brethren cheered. It was at that moment that legend of the Skrill Tamer was born."
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e79787dd-6d2f-4773-aefe-16a4eb93d3cc.jpg?1783942050"
    }
}
