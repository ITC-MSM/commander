package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Summit Sentinel
 * {1}{U}
 * Creature — Elemental Soldier
 * 1/3
 * When this creature dies, draw a card.
 */
val SummitSentinel = card("Summit Sentinel") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Soldier"
    power = 1
    toughness = 3
    oracleText = "When this creature dies, draw a card."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Jake Murray"
        flavorText = "No visitors make the ascent of Mount Tanufel without catching the eye of its rimekin guardians."
        imageUri = "https://cards.scryfall.io/normal/front/8/1/81251057-f270-4f05-9dc5-205c70e1f295.jpg?1767957040"
    }
}
