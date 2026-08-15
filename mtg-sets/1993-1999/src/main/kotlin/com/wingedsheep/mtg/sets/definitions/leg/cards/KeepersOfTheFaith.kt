package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Keepers of the Faith
 * {1}{W}{W}
 * Creature — Human Cleric
 * 2/3
 *
 * Vanilla — no rules text.
 */
val KeepersOfTheFaith = card("Keepers of the Faith") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Daniel Gelon"
        flavorText = "And then the Archangel Anthius spoke to them, saying, \"Fear shall be vanquished by the Sword of Faith.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b63a69ae-99ce-4d26-88b7-784793c43cd4.jpg?1783948083"
    }
}
