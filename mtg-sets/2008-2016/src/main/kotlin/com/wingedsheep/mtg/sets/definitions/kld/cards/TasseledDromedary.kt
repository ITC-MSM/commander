package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tasseled Dromedary
 * {W}
 * Creature — Camel
 * 0/4
 *
 * Vanilla — no rules text.
 */
val TasseledDromedary = card("Tasseled Dromedary") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Camel"
    power = 0
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Raoul Vitale"
        flavorText = "There is no dress code for the Inventors' Fair, but you'll be hard-pressed to find anyone or anything not done up in their finest."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9cef3bf2-55cf-4f42-9ec0-fa921ef22311.jpg?1783937227"
    }
}
