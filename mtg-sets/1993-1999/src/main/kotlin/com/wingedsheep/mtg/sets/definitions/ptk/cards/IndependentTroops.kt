package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Independent Troops
 * {1}{R}
 * Creature — Human Soldier
 * 2/1
 *
 * Vanilla — no rules text.
 */
val IndependentTroops = card("Independent Troops") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Kuang Sheng"
        flavorText = "\"The empire, long united, must divide, and long divided, must unite.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff7a4769-7a64-4016-8db0-b56c6b98aff3.jpg?1783946106"
    }
}
