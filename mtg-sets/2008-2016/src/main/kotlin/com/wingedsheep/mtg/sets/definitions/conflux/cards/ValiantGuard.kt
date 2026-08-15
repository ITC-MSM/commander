package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Valiant Guard
 * {W}
 * Creature — Human Soldier
 * 0/3
 *
 * Vanilla — no rules text.
 */
val ValiantGuard = card("Valiant Guard") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 0
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Chris Rahn"
        flavorText = "As the outsiders invaded Bant, soldiers who once saw sigils as the highest marks of glory began to see the scars of battle as tokens of equal worth."
        imageUri = "https://cards.scryfall.io/normal/front/8/3/83ec1486-900b-4763-9b5b-390cb00aff02.jpg?1783942490"
    }
}
