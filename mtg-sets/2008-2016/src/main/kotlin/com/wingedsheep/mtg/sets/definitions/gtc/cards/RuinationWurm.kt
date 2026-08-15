package com.wingedsheep.mtg.sets.definitions.gtc.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ruination Wurm
 * {4}{R}{G}
 * Creature — Wurm
 * 7/6
 *
 * Vanilla — no rules text.
 */
val RuinationWurm = card("Ruination Wurm") {
    manaCost = "{4}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Wurm"
    power = 7
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "192"
        artist = "Dave Kendall"
        flavorText = "When the architects of Ravnica claim a structure will stand against a wurm, they never mention for how long."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce04d1ee-2605-472d-b3ee-24800342e9af.jpg?1783940101"
    }
}
