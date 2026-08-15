package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect

/**
 * Rukh Egg
 * {3}{R}
 * Creature — Bird Egg
 * 0/3
 * When this creature dies, create a 4/4 red Bird creature token with flying
 * at the beginning of the next end step.
 */
val RukhEgg = card("Rukh Egg") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Bird Egg"
    power = 0
    toughness = 3
    oracleText = "When this creature dies, create a 4/4 red Bird creature token with flying at the beginning of the next end step."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = CreateDelayedTriggerEffect(
            step = Step.END,
            effect = Effects.CreateToken(
                power = 4,
                toughness = 4,
                colors = setOf(Color.RED),
                creatureTypes = setOf("Bird"),
                keywords = setOf(Keyword.FLYING),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Christopher Rush"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b28f9e63-e5e4-44b5-a17e-8301ff17c623.jpg?1562928213"
    }
}
