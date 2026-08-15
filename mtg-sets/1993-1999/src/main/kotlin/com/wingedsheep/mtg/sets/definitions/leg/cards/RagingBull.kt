package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Raging Bull
 * {2}{R}
 * Creature — Ox
 * 2/2
 *
 * Vanilla — no rules text.
 */
val RagingBull = card("Raging Bull") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ox"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "Randy Asplund-Faith"
        flavorText = "\"Sometimes the bulls win, and sometimes the bears win. But the bulls have more fun.\"\n—Anonymous"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec10a51c-d2c3-4d14-9a71-9e59155bf980.jpg?1783948052"
    }
}
