package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jerrard of the Closed Fist
 * {3}{R}{G}{G}
 * Legendary Creature — Human Knight
 * 6/5
 *
 * Vanilla — no rules text.
 */
val JerrardOfTheClosedFist = card("Jerrard of the Closed Fist") {
    manaCost = "{3}{R}{G}{G}"
    colorIdentity = "RG"
    typeLine = "Legendary Creature — Human Knight"
    power = 6
    toughness = 5

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "235"
        artist = "Andi Rusu"
        flavorText = "Once, the order of the Closed Fist reached throughout the Kb'Briann Highlands. Now, Jerrard alone remains to uphold their standard."
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f841918-813b-4784-ab57-907185b0a355.jpg?1783948038"
    }
}
