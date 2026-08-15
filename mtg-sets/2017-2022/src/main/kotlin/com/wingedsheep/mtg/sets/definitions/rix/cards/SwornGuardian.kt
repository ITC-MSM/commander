package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sworn Guardian
 * {1}{U}
 * Creature — Merfolk Warrior
 * 1/3
 *
 * Vanilla — no rules text.
 */
val SwornGuardian = card("Sworn Guardian") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Warrior"
    power = 1
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "Sara Winters"
        flavorText = "For the River Heralds, the Immortal Sun is an object of terror and devastation. The idea that anyone would retrieve it for their own use is utterly abhorrent."
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6452ba94-6bb0-409c-99f7-71e6457c3f2a.jpg?1783935317"
    }
}
