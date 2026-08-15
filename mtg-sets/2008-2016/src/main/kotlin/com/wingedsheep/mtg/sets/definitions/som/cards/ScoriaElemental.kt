package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scoria Elemental
 * {4}{R}
 * Creature — Elemental
 * 6/1
 *
 * Vanilla — no rules text.
 */
val ScoriaElemental = card("Scoria Elemental") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 6
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Karl Kopinski"
        flavorText = "A single molten cord links it to the subterranean furnaces, drawing heat and metal from beneath Kuldotha. Until that bond is cut, it carves a swath of raw destruction."
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca4d9198-52a7-4dfe-8f7f-4fa6e19a2479.jpg?1783941723"
    }
}
