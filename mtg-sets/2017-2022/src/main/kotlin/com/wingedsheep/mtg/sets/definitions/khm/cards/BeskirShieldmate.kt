package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Beskir Shieldmate
 * {1}{W}
 * Creature — Human Warrior
 * 2/1
 * When this creature dies, create a 1/1 white Human Warrior creature token.
 *
 * A plain dies trigger. [Triggers.Dies] already fires from the graveyard, so the token is created
 * after the Shieldmate has left the battlefield.
 */
val BeskirShieldmate = card("Beskir Shieldmate") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Warrior"
    oracleText = "When this creature dies, create a 1/1 white Human Warrior creature token."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Warrior")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Manuel Castañón"
        flavorText = "\"If we fall today, let us fall with honor, defending our realm from the horrors that would defile it. Forward, shieldmates! To Starnheim!\""
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfc1df84-9c47-444b-9d58-d9c7bed51c66.jpg"
    }
}
