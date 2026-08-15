package com.wingedsheep.mtg.sets.definitions.bok.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Gnarled Mass
 * {1}{G}{G}
 * Creature — Spirit
 * 3/3
 *
 * Vanilla — no rules text.
 */
val GnarledMass = card("Gnarled Mass") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Tony Szczudlo"
        flavorText = "\"On the fifty-seventh day of the Battle of Silk, the bell again tolled in hopes of summoning mortal aid. This time, a new breed of kami rose to answer its call.\"\n—*Great Battles of Kamigawa*"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c28728d-4839-4cdf-91d4-b9fb4b5d0449.jpg?1783944185"
    }
}
