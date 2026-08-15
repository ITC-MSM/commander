package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pheres-Band Centaurs
 * {4}{G}
 * Creature — Centaur Warrior
 * 3/7
 *
 * Vanilla — no rules text.
 */
val PheresBandCentaurs = card("Pheres-Band Centaurs") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Centaur Warrior"
    power = 3
    toughness = 7

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "171"
        artist = "Mark Winters"
        flavorText = "\"Poets speak of your unrivaled speed,\" the Champion said to the assembled centaurs, \"but it is plain to see that your true strength lies in your unwavering loyalty to one another.\"\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/2168fcf4-cf87-4ab8-9710-6ec672750a9a.jpg?1783939739"
    }
}
