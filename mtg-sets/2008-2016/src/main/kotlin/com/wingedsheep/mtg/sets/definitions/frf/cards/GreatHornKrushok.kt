package com.wingedsheep.mtg.sets.definitions.frf.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Great-Horn Krushok
 * {4}{W}
 * Creature — Beast
 * 3/5
 *
 * Vanilla — no rules text.
 */
val GreatHornKrushok = card("Great-Horn Krushok") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 5

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "YW Tang"
        flavorText = "Perfectly suited for life in the Shifting Wastes, the krushok is well protected by its horn, its hide, and its temper."
        imageUri = "https://cards.scryfall.io/normal/front/1/2/122e08cb-407b-4b3d-8af0-077ff96bf160.jpg?1783938713"
    }
}
