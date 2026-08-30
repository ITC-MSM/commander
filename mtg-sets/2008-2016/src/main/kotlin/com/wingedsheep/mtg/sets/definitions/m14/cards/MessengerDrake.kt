package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Messenger Drake
 * {3}{U}{U}
 * Creature — Drake
 * 3 / 3
 * Flying
 * When this creature dies, draw a card.
 */
val MessengerDrake = card("Messenger Drake") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
            "When this creature dies, draw a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
        description = "When this creature dies, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Yeong-Hao Han"
        flavorText = "The more important the message, the larger the messenger."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13dd3172-0b45-4dc8-adc6-9e0ba112e664.jpg"
    }
}
