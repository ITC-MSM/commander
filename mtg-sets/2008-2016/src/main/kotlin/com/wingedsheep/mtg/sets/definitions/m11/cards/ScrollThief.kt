package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scroll Thief
 * {2}{U}
 * Creature — Merfolk Rogue
 * 1/3
 *
 * Whenever this creature deals combat damage to a player, draw a card.
 */
val ScrollThief = card("Scroll Thief") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Rogue"
    power = 1
    toughness = 3
    oracleText = "Whenever this creature deals combat damage to a player, draw a card."

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Alex Horley-Orlandelli"
        flavorText = "I've learned how to disable wards, pick locks, and decode the Agaran language—all before even reading the scroll!"
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f3b2808-58d9-4e27-a6c2-6db66191151e.jpg?1783941822"
    }
}
