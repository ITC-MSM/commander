package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Y'shtola Rhul
 * {4}{U}{U}
 * Legendary Creature — Cat Druid
 * 3/5
 *
 * At the beginning of your end step, exile target creature you control, then return it to the
 * battlefield under its owner's control. Then if it's the first end step of the turn, there is an
 * additional end step after this step.
 *
 * The blink is the standard immediate exile-then-return ([Effects.Move] to exile, then back to the
 * battlefield — cards return under their owner's control by default). The "additional end step"
 * rider is gated behind [Conditions.IsFirstEndStepOfTurn] (CR 500.9 / 513): the trigger fires again
 * during the extra end step it creates, but by then the active player is flagged as being in an
 * inserted end step, so the condition is false and no further end step is spawned — exactly one
 * extra end step per turn.
 */
val YshtolaRhul = card("Y'shtola Rhul") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Cat Druid"
    power = 3
    toughness = 5
    oracleText = "At the beginning of your end step, exile target creature you control, then " +
        "return it to the battlefield under its owner's control. Then if it's the first end step " +
        "of the turn, there is an additional end step after this step."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.Move(creature, Zone.EXILE)
            .then(Effects.Move(creature, Zone.BATTLEFIELD))
            .then(
                ConditionalEffect(
                    condition = Conditions.IsFirstEndStepOfTurn,
                    effect = Effects.AddAdditionalEndSteps(1)
                )
            )
        description = "At the beginning of your end step, exile target creature you control, then " +
            "return it to the battlefield under its owner's control. Then if it's the first end " +
            "step of the turn, there is an additional end step after this step."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "86"
        artist = "Immanuela Crovius"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aef218fa-13a4-4653-95d6-6b3ef1b33a92.jpg?1748706086"
    }
}
