package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Witch's Familiar
 * {2}{B}
 * Creature — Frog
 * 2/3
 *
 * Vanilla — no rules text.
 */
val WitchsFamiliar = card("Witch's Familiar") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Frog"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Jack Wang"
        flavorText = "Some bog witches practice the strange art of batrachomancy, reading portents in the number, size, and color of warts on a toad's hide."
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c9f3b3b-de16-4ae5-844e-1373e0f84469.jpg?1783939178"
    }
}
