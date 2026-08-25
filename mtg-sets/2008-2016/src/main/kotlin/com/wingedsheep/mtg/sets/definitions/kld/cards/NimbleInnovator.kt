package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nimble Innovator
 * {3}{U}
 * Creature — Vedalken Artificer
 * 2/2
 *
 * When this creature enters, draw a card.
 */
val NimbleInnovator = card("Nimble Innovator") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vedalken Artificer"
    oracleText = "When this creature enters, draw a card."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "Slawomir Maniak"
        flavorText = "\"A failure is simply another opportunity for improvement. Just wait until you see what I come up with next.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6dbf333-23b5-47d9-9e55-1e8fbd5a72cb.jpg?1783937216"
    }
}
