package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Quicksilver Fisher
 * {3}{U}{U}
 * Creature — Phyrexian Drake
 * 4/3
 *
 * Flying
 * When this creature enters, draw a card, then discard a card.
 */
val QuicksilverFisher = card("Quicksilver Fisher") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Phyrexian Drake"
    power = 4
    toughness = 3
    oracleText = "Flying\n" +
        "When this creature enters, draw a card, then discard a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.loot()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Filip Burburan"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/bad0e96a-b4cc-4439-aab9-731a1036145d.jpg?1783918058"
    }
}
