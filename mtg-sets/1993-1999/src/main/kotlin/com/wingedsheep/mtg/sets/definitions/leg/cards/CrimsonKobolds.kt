package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Crimson Kobolds
 * {0}
 * Creature — Kobold
 * 0/1
 *
 * Vanilla — no rules text.
 */
val CrimsonKobolds = card("Crimson Kobolds") {
    manaCost = "{0}"
    colorIdentity = "R"
    typeLine = "Creature — Kobold"
    power = 0
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "139"
        artist = "Anson Maddocks"
        flavorText = "\"Kobolds are harmless.\" —Bearand the Bold, epitaph"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13696657-aeef-4add-9a3b-8137fce01fe3.jpg?1783948057"
    }
}
