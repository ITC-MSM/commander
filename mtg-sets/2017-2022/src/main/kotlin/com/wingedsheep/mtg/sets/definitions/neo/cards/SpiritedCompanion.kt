package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Spirited Companion
 * {1}{W}
 * Enchantment Creature — Dog
 * 1/1
 * When this creature enters, draw a card.
 */
val SpiritedCompanion = card("Spirited Companion") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment Creature — Dog"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, draw a card."

    // When this creature enters, draw a card.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Ilse Gort"
        flavorText = "She formed a friendship with several playful spirits, and soon \"the pack\" was known as the source of much mischief in Eiganjo."
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5aa91a9e-2fe2-43bc-aa9c-cfb8a71829ff.jpg?1783923912"
    }
}
