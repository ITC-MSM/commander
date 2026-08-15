package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bogstomper
 * {4}{B}{B}
 * Creature — Beast
 * 6/5
 *
 * Vanilla — no rules text.
 */
val Bogstomper = card("Bogstomper") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Beast"
    power = 6
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Jason Felix"
        flavorText = "\"They are gentle herbivores, despite their size. Approach cautiously, and hum a tune to let them know you mean no harm.\"\n—Vivien Reid"
        imageUri = "https://cards.scryfall.io/normal/front/0/5/05145a8d-0bfb-4f07-87cf-65875310bdb4.jpg?1783934576"
    }
}
