package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tarpan
 * {G}
 * Creature — Horse
 * 1/1
 *
 * When this creature dies, you gain 1 life.
 *
 * [Triggers.Dies] is the SELF-bound battlefield-to-graveyard `ZoneChangeEvent`, so the trigger
 * needs no last-known-information reads of its own — the payoff is a plain [Effects.GainLife]
 * onto the controller, which is that facade's default target.
 */
val Tarpan = card("Tarpan") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Horse"
    power = 1
    toughness = 1
    oracleText = "When this creature dies, you gain 1 life."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "267"
        artist = "Margaret Organ-Kean"
        flavorText = "\"A good Tarpan will serve you, faithful and true. A bad one will kick you in the head.\"\n—General Jarkeld, the Arctic Fox"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1420ec5-367c-4514-86c5-3993bf339e37.jpg"
    }
}
