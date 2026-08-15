package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kobolds of Kher Keep
 * {0}
 * Creature — Kobold
 * 0/1
 *
 * Vanilla — no rules text.
 */
val KoboldsOfKherKeep = card("Kobolds of Kher Keep") {
    manaCost = "{0}"
    colorIdentity = "R"
    typeLine = "Creature — Kobold"
    power = 0
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Julie Baroh"
        flavorText = "Kher Keep is unique among fortresses: impervious to aerial assault but defenseless from the ground."
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df0320d9-7c2a-456a-9159-1b4fae67bfb5.jpg?1783948054"
    }
}
