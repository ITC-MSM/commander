package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lagac Lizard
 * {3}{R}
 * Creature — Lizard
 * 3/3
 *
 * Vanilla — no rules text.
 */
val LagacLizard = card("Lagac Lizard") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Svetlin Velinov"
        flavorText = "Tracing their ancestry back to Zendikar's earliest forms of life, lagac lizards have seen the comings and goings of planeswalkers and the Eldrazi, and the rise of the vampire clans, none of which has changed them one bit."
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b47e9cfa-5547-4ef3-9e36-8d0f36dfa59a.jpg?1783941974"
    }
}
