package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Feral Maaka
 * {1}{R}
 * Creature — Cat
 * 2/2
 *
 * Vanilla — no rules text.
 */
val FeralMaaka = card("Feral Maaka") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cat"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Jonathan Kuo"
        flavorText = "\"Lost are the lush meadows and verdant forests, where maaka prowled and lammasu soared. Lost are the wilds, where our hearts were free.\"\n—Daiva, Gruul storyteller"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c969aa0-b0e5-42cd-abba-0a3c7266142c.jpg?1783933682"
    }
}
