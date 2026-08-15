package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Armored Cancrix
 * {4}{U}
 * Creature — Crab
 * 2/5
 *
 * Vanilla — no rules text.
 */
val ArmoredCancrix = card("Armored Cancrix") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Crab"
    power = 2
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Tomasz Jedruszek"
        flavorText = "Creatures displaced from time still turn up every year, stranded by the temporal disaster that once swept across Dominaria."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53ef0757-8eb0-4384-bf8e-9a7340144dfa.jpg?1783941828"
    }
}
