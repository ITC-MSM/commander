package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Muse Drake
 * {3}{U}
 * Creature — Drake
 * 1/3
 * Flying
 * When this creature enters, draw a card.
 */
val MuseDrake = card("Muse Drake") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText = "Flying\n" +
        "When this creature enters, draw a card."
    power = 1
    toughness = 3

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Titus Lunter"
        flavorText = "A composer wrote a symphony based on the drakes screeching outside her window. Reviews were mixed—except among the drakes."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5df7a96-6548-41c0-85a6-e0c4566e0fe6.jpg?1783934187"
    }
}
