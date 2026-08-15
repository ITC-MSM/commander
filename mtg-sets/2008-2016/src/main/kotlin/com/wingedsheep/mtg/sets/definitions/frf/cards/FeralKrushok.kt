package com.wingedsheep.mtg.sets.definitions.frf.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Feral Krushok
 * {4}{G}
 * Creature — Beast
 * 5/4
 *
 * Vanilla — no rules text.
 */
val FeralKrushok = card("Feral Krushok") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 4

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Kev Walker"
        flavorText = "In a stunning act of diplomacy, Yasova Dragonclaw ceded a portion of Temur lands to the Sultai. Her clan protested until they saw she had given the Sultai the breeding grounds of the krushoks. They hadn't realized she had a sense of humor."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/5041996b-c265-4c4f-a52c-dfe29b2e282d.jpg?1783938681"
    }
}
