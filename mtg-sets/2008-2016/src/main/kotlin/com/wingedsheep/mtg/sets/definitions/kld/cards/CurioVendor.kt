package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Curio Vendor
 * {1}{U}
 * Creature — Vedalken
 * 2/1
 *
 * Vanilla — no rules text.
 */
val CurioVendor = card("Curio Vendor") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vedalken"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "42"
        artist = "Igor Kieryluk"
        flavorText = "\"Step right up! Try your hand! It'll thrill the senses and boggle the mind!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c598054a-26fa-40e7-8497-3da8eaf12aac.jpg?1783937222"
    }
}
