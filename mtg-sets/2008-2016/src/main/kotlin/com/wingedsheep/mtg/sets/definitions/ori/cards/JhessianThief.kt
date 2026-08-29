package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jhessian Thief
 * {2}{U}
 * Creature — Human Rogue
 * 1/3
 *
 * Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * Whenever this creature deals combat damage to a player, draw a card.
 */
val JhessianThief = card("Jhessian Thief") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Rogue"
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "Whenever this creature deals combat damage to a player, draw a card."
    power = 1
    toughness = 3

    prowess()

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Miles Johnston"
        flavorText = "\"Where's the fun in an escape if it's not at least a little daring?\""
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33b8553d-d326-4280-bc3a-2fffdd377cd2.jpg"
    }
}
