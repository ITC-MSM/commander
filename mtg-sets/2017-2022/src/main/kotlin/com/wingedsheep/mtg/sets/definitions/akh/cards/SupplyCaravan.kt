package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Supply Caravan
 * {4}{W}
 * Creature — Camel
 * 3/5
 * When this creature enters, if you control a tapped creature, create a 1/1 white Warrior creature token with vigilance.
 *
 * The "if you control a tapped creature" clause is printed straight after the trigger event, so it
 * is an intervening-if (CR 603.4) — checked both when the trigger would go on the stack and again
 * on resolution.
 */
val SupplyCaravan = card("Supply Caravan") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Camel"
    oracleText = "When this creature enters, if you control a tapped creature, create a 1/1 white Warrior creature token with vigilance."
    power = 3
    toughness = 5

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.YouControl(GameObjectFilter.Creature.tapped())
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Warrior"),
            keywords = setOf(Keyword.VIGILANCE),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Nils Hamm"
        flavorText = "\"We each have a weight to carry on the road to the afterlife.\"\n—Oketra, god of solidarity"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d247bd88-9593-462c-8f9e-28e29c064c10.jpg?1783936532"
    }
}
