package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Squire
 * {1}{W}
 * Creature — Human Soldier
 * 1/2
 *
 * Vanilla — no rules text.
 */
val Squire = card("Squire") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Dennis Detwiller"
        flavorText = "\"Of twenty yeer of age he was, I gesse.\nOf his stature he was of even lengthe,\nAnd wonderly deliver, and greete of strengthe.\"\n—Geoffrey Chaucer, *The Canterbury Tales*"
        imageUri = "https://cards.scryfall.io/normal/front/3/7/374df061-ebd2-4f1f-9a6e-7940a49197a9.jpg?1783947948"
    }
}
