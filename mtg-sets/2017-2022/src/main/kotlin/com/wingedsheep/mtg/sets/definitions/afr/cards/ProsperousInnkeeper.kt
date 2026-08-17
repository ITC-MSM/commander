package com.wingedsheep.mtg.sets.definitions.afr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Prosperous Innkeeper
 * {1}{G}
 * Creature — Halfling Citizen
 * 1/1
 *
 * When this creature enters, create a Treasure token.
 * Whenever another creature you control enters, you gain 1 life.
 */
val ProsperousInnkeeper = card("Prosperous Innkeeper") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Halfling Citizen"
    oracleText = "When this creature enters, create a Treasure token. (It's an artifact with " +
        "\"{T}, Sacrifice this token: Add one mana of any color.\")\n" +
        "Whenever another creature you control enters, you gain 1 life."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateTreasure()
    }

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "200"
        artist = "Eric Deschamps"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/096d3c0c-98e2-4cfc-a6e1-fddb0359c63f.jpg?1783926456"
    }
}
