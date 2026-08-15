package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Looming Altisaur
 * {3}{W}
 * Creature — Dinosaur
 * 1/7
 *
 * Vanilla — no rules text.
 */
val LoomingAltisaur = card("Looming Altisaur") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dinosaur"
    power = 1
    toughness = 7

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Lars Grant-West"
        flavorText = "Nature can't be tamed, but the Sun Empire believes that humans are made stronger when they test themselves against the wild strength of the dinosaurs."
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfcff1c6-0db6-4ff6-b4af-d7048b426368.jpg?1783935797"
    }
}
