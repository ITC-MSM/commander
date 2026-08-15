package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Silvercoat Lion
 * {1}{W}
 * Creature — Cat
 * 2/2
 *
 * Vanilla — no rules text.
 */
val SilvercoatLion = card("Silvercoat Lion") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Terese Nielsen"
        flavorText = "Hunters of every species on the savannah, silvercoats disdain camouflage in favor of total dominance of the food chain."
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea82996f-a05f-4831-9bbd-3281ebca9a61.jpg?1783942398"
    }
}
