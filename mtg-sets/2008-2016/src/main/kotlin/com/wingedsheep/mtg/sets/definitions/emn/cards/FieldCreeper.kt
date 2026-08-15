package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Field Creeper
 * {2}
 * Artifact Creature — Scarecrow
 * 2/1
 *
 * Vanilla — no rules text.
 */
val FieldCreeper = card("Field Creeper") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Scarecrow"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "195"
        artist = "Anthony Palumbo"
        flavorText = "As it walks across the fallow field, its awkward, loping gait matches the rattling in its head to create a haunting rhythm that chills the bones."
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b92b162-f51d-4138-8d9b-e5eb929ad87e.jpg?1783937424"
    }
}
