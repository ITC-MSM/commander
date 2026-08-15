package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wicker Witch
 * {3}
 * Artifact Creature — Scarecrow
 * 3/1
 *
 * Vanilla — no rules text.
 */
val WickerWitch = card("Wicker Witch") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Scarecrow"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "268"
        artist = "Izzy"
        flavorText = "When there were no more crows to scare, it focused its efforts elsewhere."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae115587-012d-40ff-a20d-270fabf2f8c6.jpg?1783937699"
    }
}
