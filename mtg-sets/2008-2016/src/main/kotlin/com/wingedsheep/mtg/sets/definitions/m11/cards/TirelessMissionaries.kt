package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tireless Missionaries
 * {4}{W}
 * Creature — Human Cleric
 * 2/3
 * When this creature enters, you gain 3 life.
 *
 * Canonical printing: Magic 2011, the card's earliest real-expansion printing. Reprinted in M15 as
 * a `Printing` row.
 */
val TirelessMissionaries = card("Tireless Missionaries") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 3
    oracleText = "When this creature enters, you gain 3 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
        description = "When this creature enters, you gain 3 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Dave Kendall"
        flavorText = "If they succeed in their holy work, their order will vanish into welcome obscurity, for there will be no more souls to redeem."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc42fe32-5f02-472c-a25e-d03e01546dc6.jpg?1783941830"
    }
}
