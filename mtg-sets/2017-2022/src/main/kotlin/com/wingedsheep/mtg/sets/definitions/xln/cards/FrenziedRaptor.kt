package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Frenzied Raptor
 * {2}{R}
 * Creature — Dinosaur
 * 4/2
 *
 * Vanilla — no rules text.
 */
val FrenziedRaptor = card("Frenzied Raptor") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    power = 4
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Jesper Ejsing"
        flavorText = "Sun Empire warriors are taught to emulate the fearless raptors that fling themselves against towers of horn and muscle a hundred times their size."
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7c3a1c9-ffa2-4990-aa4b-db9d688f1ed4.jpg?1783935745"
    }
}
