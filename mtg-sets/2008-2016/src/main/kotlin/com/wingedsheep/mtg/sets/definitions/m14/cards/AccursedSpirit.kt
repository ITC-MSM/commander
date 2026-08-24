package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Accursed Spirit
 * {3}{B}
 * Creature — Spirit
 * 3/2
 * Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)
 *
 * CR 702.13b — enforced by `IntimidateRule` in the engine's block-evasion chain.
 */
val AccursedSpirit = card("Accursed Spirit") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 2
    oracleText = "Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that share a color with it.)"

    keywords(Keyword.INTIMIDATE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "Kev Walker"
        flavorText = "Many have heard the slither of dragging armor and the soft squelch of its voice. But only its victims ever meet its icy gaze."
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf08313b-14c9-4e0b-aad7-05cbd90b1ed8.jpg?1783939927"
    }
}
