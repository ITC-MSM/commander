package com.wingedsheep.mtg.sets.definitions.mor.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Indomitable Ancients
 * {2}{W}{W}
 * Creature — Treefolk Warrior
 * 2/10
 *
 * Vanilla — no rules text.
 */
val IndomitableAncients = card("Indomitable Ancients") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Treefolk Warrior"
    power = 2
    toughness = 10

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "13"
        artist = "Pete Venters"
        flavorText = "\"Odum and Broadbark were the only beings mighty enough to challenge the giant Moran the Destroyer. Their battle lasted a hundred dawns, until Moran became so exhausted that he fell into namesleep. He awoke as Moran the Gardener.\"\n—*The Tale of Odum and Broadbark*"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/ddf33cb6-d159-4af3-8403-d0ac10af9894.jpg?1783942804"
    }
}
