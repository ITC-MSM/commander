package com.wingedsheep.mtg.sets.definitions.aer.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Prizefighter Construct
 * {5}
 * Artifact Creature — Construct
 * 6/2
 *
 * Vanilla — no rules text.
 */
val PrizefighterConstruct = card("Prizefighter Construct") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 6
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "172"
        artist = "Daniel Ljunggren"
        flavorText = "The Scrappers, an underground group with a passion for automaton brawls, had renegade leanings even before the Consulate's crackdown. It didn't take long for them to lend their best brawlers to the conflict."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e389c92-b54b-46b3-a7ab-b8a5a2a7d380.jpg?1783936720"
    }
}
