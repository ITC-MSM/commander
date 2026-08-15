package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Canyon Minotaur
 * {3}{R}
 * Creature — Minotaur Warrior
 * 3/3
 *
 * Vanilla — no rules text.
 */
val CanyonMinotaur = card("Canyon Minotaur") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Warrior"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Steve Prescott"
        flavorText = "On Jund, the deep canyons were the best places to hide. When the goblins wandered into Naya, they found that was not so true."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b200790-43c7-42ae-9edf-89c8198a385b.jpg?1783942480"
    }
}
