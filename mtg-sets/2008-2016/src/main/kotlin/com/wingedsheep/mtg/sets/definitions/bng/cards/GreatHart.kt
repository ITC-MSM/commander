package com.wingedsheep.mtg.sets.definitions.bng.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Great Hart
 * {3}{W}
 * Creature — Elk
 * 2/4
 *
 * Vanilla — no rules text.
 */
val GreatHart = card("Great Hart") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elk"
    power = 2
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Christopher Moeller"
        flavorText = "The great hart stood like a statue, its hide painted gold by the dawn. The Champion laid down her weapons and stepped forward within an arm's length of the beast. The hart, sacred to Heliod and bathed in the god's own light, bowed to the Champion, marking her as the Chosen of the Sun God.\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70cd7d2b-e9c4-4900-89a0-f6eb0c6cb22b.jpg?1783939581"
    }
}
