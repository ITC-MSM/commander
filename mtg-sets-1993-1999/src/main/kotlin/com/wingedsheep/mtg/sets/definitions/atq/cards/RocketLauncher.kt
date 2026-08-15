package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rocket Launcher
 * {4}
 * Artifact
 * {2}: This artifact deals 1 damage to any target. Destroy this artifact at the beginning of the
 *   next end step. Activate only if you've controlled this artifact continuously since the
 *   beginning of your most recent turn.
 *
 * Modeling notes:
 * - The activation timing clause is the artifact "summoning sickness" condition
 *   ([ActivationRestriction.ControlledSinceYourMostRecentTurn]): the ability can't be used the turn
 *   Rocket Launcher enters, nor the turn its control changes, but becomes available once it has been
 *   under your control since the start of your most recent turn.
 * - "Destroy this artifact at the beginning of the next end step" is a delayed trigger scheduled by
 *   the ability ([CreateDelayedTriggerEffect] at [Step.END]).
 */
val RocketLauncher = card("Rocket Launcher") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}: This artifact deals 1 damage to any target. Destroy this artifact at the " +
        "beginning of the next end step. Activate only if you've controlled this artifact " +
        "continuously since the beginning of your most recent turn."

    activatedAbility {
        cost = Costs.Mana("{2}")
        val t = target("any target", AnyTarget())
        effect = Effects.Composite(
            Effects.DealDamage(1, t),
            CreateDelayedTriggerEffect(
                step = Step.END,
                effect = Effects.Destroy(EffectTarget.Self)
            )
        )
        restrictions = listOf(ActivationRestriction.ControlledSinceYourMostRecentTurn)
        description = "{2}: Rocket Launcher deals 1 damage to any target. Destroy it at the beginning " +
            "of the next end step. Activate only if you've controlled it continuously since your most recent turn began."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Pete Venters"
        flavorText = "What these devices lacked in subtlety, they made up in strength."
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5bb2093-78a8-4a6c-abe7-9a5afc181ec5.jpg?1750264519"
    }
}
