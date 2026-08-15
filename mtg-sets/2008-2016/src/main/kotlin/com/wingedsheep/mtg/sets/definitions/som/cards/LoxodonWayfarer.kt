package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Loxodon Wayfarer
 * {2}{W}
 * Creature — Elephant Monk
 * 1/5
 *
 * Vanilla — no rules text.
 */
val LoxodonWayfarer = card("Loxodon Wayfarer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant Monk"
    power = 1
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Steven Belledin"
        flavorText = "The Mirran elders vanished with Memnarch, leaving behind a generation of wayward orphans."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/356c5e6a-c0bd-43f7-bc84-a6ae8718a7a2.jpg?1783941744"
    }
}
