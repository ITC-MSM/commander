package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soulmender
 * {W}
 * Creature — Human Cleric
 * 1/1
 * {T}: You gain 1 life.
 *
 * Canonical printing: Magic 2014, the card's earliest real-expansion printing. Reprinted in M15,
 * M20 and ANB as `Printing` rows.
 */
val Soulmender = card("Soulmender") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "{T}: You gain 1 life."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "James Ryman"
        flavorText = "\"Healing is more art than magic. Well, there is still quite a bit of magic.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/7/37f45133-6134-4664-9952-67c03d60f9a0.jpg?1783939939"
    }
}
