package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Russet Wolves
 * {3}{R}
 * Creature — Wolf
 * 3/3
 *
 * Vanilla — no rules text.
 */
val RussetWolves = card("Russet Wolves") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wolf"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Christopher Moeller"
        flavorText = "\"The wolves of our valley are bred to detest the scent of ghouls. For centuries they have kept our estates clear of such carrion.\"\n—Olivia Voldaren"
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3c7c972-5a11-4709-b3ef-e2acb3b51dd9.jpg?1783940814"
    }
}
