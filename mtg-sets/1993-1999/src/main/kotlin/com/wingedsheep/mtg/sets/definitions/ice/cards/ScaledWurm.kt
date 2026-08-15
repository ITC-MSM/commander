package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scaled Wurm
 * {7}{G}
 * Creature — Wurm
 * 7/6
 *
 * Vanilla — no rules text.
 */
val ScaledWurm = card("Scaled Wurm") {
    manaCost = "{7}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 7
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "262"
        artist = "Daniel Gelon"
        flavorText = "\"Flourishing during the Ice Age, these Wurms were the bane of all Kjeldorans. Their great size and ferocity made them the subject of countless nightmares—they embodied the worst of the Ice Age.\"\n—*Kjeldor: Ice Civilization*"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/499cd7fa-c86c-4a5f-b36d-8160e8a6af1f.jpg?1783947472"
    }
}
