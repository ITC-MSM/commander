package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity


/**
 * Hecteyes
 * {1}{B}
 * Creature — Ooze Horror
 * 1/1
 * When this creature enters, each opponent discards a card.
 */
val Hecteyes = card("Hecteyes") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Ooze Horror"
    oracleText = "When this creature enters, each opponent discards a card."
    power = 1
    toughness = 1
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.eachOpponentDiscards(1)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "SHOSUKE"
        flavorText = "\"Once, the monsters of Hell poured forth into the world. They entered using a path known as the Jade Passage.\"\n—Hilda, Princess of Fynn"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/8680d052-c07b-4d9b-bda9-b5f69f44f424.jpg"
    }
}
