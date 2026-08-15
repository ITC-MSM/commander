package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Barony Vampire
 * {2}{B}
 * Creature — Vampire
 * 3/2
 *
 * Vanilla — no rules text.
 */
val BaronyVampire = card("Barony Vampire") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Daarken"
        flavorText = "\"Poor little sun-dweller out past curfew. And to think, you might have survived if it wasn't so close to suppertime.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88583170-7b0e-4c02-b270-4859ba05d82b.jpg?1783941820"
    }
}
