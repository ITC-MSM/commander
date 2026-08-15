package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Minotaur Abomination
 * {4}{B}{B}
 * Creature — Zombie Minotaur
 * 4/6
 *
 * Vanilla — no rules text.
 */
val MinotaurAbomination = card("Minotaur Abomination") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Minotaur"
    power = 4
    toughness = 6

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Karl Kopinski"
        flavorText = "\"Look at that. Shuffling, wobbling, entrails placed haphazardly. It's shameful. Who would let that kind of work flap about for all to see?\"\n—Lestin, necromancer"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9dca75a1-443d-4f8e-b12b-2aada3a8e3e4.jpg?1783939921"
    }
}
