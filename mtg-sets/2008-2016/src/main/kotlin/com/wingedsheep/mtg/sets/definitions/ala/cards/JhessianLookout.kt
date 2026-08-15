package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jhessian Lookout
 * {1}{U}
 * Creature — Human Scout
 * 2/1
 *
 * Vanilla — no rules text.
 */
val JhessianLookout = card("Jhessian Lookout") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Scout"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Donato Giancola"
        flavorText = "She stands ready, always watchful, knowing that weeks of peace and serenity can be overturned by a single distant sail."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f55b1b92-575e-4b6f-9179-21d0bc1acd11.jpg?1783942574"
    }
}
