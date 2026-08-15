package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hyena Pack
 * {2}{R}{R}
 * Creature — Hyena
 * 3/4
 *
 * Vanilla — no rules text.
 */
val HyenaPack = card("Hyena Pack") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Hyena"
    power = 3
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Winona Nelson"
        flavorText = "With carrion a rarity in the Broken Lands, the hyenas that stalk the deserts hunt in packs."
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9fa8351-567e-4ef4-8346-c58e50c778a6.jpg?1783936487"
    }
}
