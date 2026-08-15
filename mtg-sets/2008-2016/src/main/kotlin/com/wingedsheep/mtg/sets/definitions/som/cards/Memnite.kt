package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Memnite
 * {0}
 * Artifact Creature — Construct
 * 1/1
 *
 * Vanilla — no rules text.
 */
val Memnite = card("Memnite") {
    manaCost = "{0}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 1
    toughness = 1

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Svetlin Velinov"
        flavorText = "Reminders of Memnarch's reign still skirr across Mirrodin, reminiscent of his form if not his power."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/469cc4e0-49c0-4009-97ea-28e44addec69.jpg?1783941703"
    }
}
