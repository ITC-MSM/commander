package com.wingedsheep.mtg.sets.definitions.nph.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flameborn Viron
 * {4}{R}{R}
 * Creature — Phyrexian Insect
 * 6/4
 *
 * Vanilla — no rules text.
 */
val FlamebornViron = card("Flameborn Viron") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Phyrexian Insect"
    power = 6
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "Svetlin Velinov"
        flavorText = "\"Large or small, all will toil for the Great Work.\"\n—Decree of Urabrask"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/9601ea62-a609-4bc5-a2f0-f7615b4dd5fa.jpg?1783941308"
    }
}
