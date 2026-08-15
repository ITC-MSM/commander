package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scream Puff
 * {4}{B}
 * Creature — Horror (4/5)
 *
 * Deathtouch
 * Whenever this creature deals combat damage to a player, create a Food token.
 *
 * Straight composition: the [Keyword.DEATHTOUCH] keyword plus a [Triggers.DealsCombatDamageToPlayer]
 * (SELF-bound) trigger that creates a Food token via [Effects.CreateFood]. Food is the shared
 * Wilds of Eldraine artifact token ("{2}, {T}, Sacrifice this token: You gain 3 life.").
 */
val ScreamPuff = card("Scream Puff") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Horror"
    power = 4
    toughness = 5
    oracleText = "Deathtouch\n" +
        "Whenever this creature deals combat damage to a player, create a Food token. " +
        "(It's an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.CreateFood()
        description = "Whenever this creature deals combat damage to a player, create a Food token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Nicholas Gregory"
        flavorText = "Too much sugar is bad for your health."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c62d0ae9-5a82-40bf-b8bb-9c2e2d55d458.jpg?1783915103"
    }
}
