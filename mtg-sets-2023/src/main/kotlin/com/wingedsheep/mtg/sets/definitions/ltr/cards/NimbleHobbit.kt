package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ChooseActionEffect
import com.wingedsheep.sdk.scripting.effects.EffectChoice
import com.wingedsheep.sdk.scripting.effects.FeasibilityCheck
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nimble Hobbit
 * {1}{W}
 * Creature — Halfling Peasant
 * 1/3
 *
 * Whenever this creature attacks, you may sacrifice a Food or pay {2}{W}.
 * When you do, tap target creature an opponent controls.
 */
val NimbleHobbit = card("Nimble Hobbit") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Halfling Peasant"
    power = 1
    toughness = 3
    oracleText = "Whenever this creature attacks, you may sacrifice a Food or pay {2}{W}. When you do, tap target creature an opponent controls."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = ReflexiveTriggerEffect(
            // "you may sacrifice a Food or pay {2}{W}"
            action = ChooseActionEffect(
                choices = listOf(
                    EffectChoice(
                        label = "Sacrifice a Food",
                        effect = SacrificeEffect(
                            filter = GameObjectFilter.Any.withSubtype("Food")
                        ),
                        feasibilityCheck = FeasibilityCheck.ControlsPermanentMatching(
                            filter = GameObjectFilter.Any.withSubtype("Food")
                        )
                    ),
                    EffectChoice(
                        label = "Pay {2}{W}",
                        effect = PayManaCostEffect(ManaCost.parse("{2}{W}"))
                    )
                )
            ),
            optional = true,
            // "When you do, tap target creature an opponent controls."
            reflexiveEffect = Effects.Tap(EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(Targets.CreatureOpponentControls)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "JB Casacop"
        flavorText = "The ruffian knew too little of Hobbits to understand his peril. Foolishly, he decided to fight."
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4cd0756-7bf3-4cb6-9687-1f9346b0bb92.jpg?1686967856"
    }
}
