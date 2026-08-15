package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Garruk's Gorehorn
 * {4}{G}
 * Creature — Beast
 * 7/3
 *
 * Vanilla — no rules text.
 */
val GarruksGorehorn = card("Garruk's Gorehorn") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 7
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "184"
        artist = "Svetlin Velinov"
        flavorText = "\"It certainly takes after its master: big and brutish, and you can smell it from a mile away.\"\n—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/3928bbce-87b7-4b28-9af4-20362935c909.jpg?1783930677"
    }
}
