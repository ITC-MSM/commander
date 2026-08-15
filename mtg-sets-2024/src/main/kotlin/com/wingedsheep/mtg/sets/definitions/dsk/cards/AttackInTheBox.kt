package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Attack-in-the-Box
 * {3}
 * Artifact Creature — Toy
 * 2/4
 * Whenever this creature attacks, you may have it get +4/+0 until end of turn. If you do,
 * sacrifice it at the beginning of the next end step.
 *
 * Modeled as an attack trigger whose whole "+4/+0 and arm the delayed sacrifice" body is gated by
 * a single "you may" decision — so declining skips both the pump and the sacrifice ("if you do"
 * binds the sacrifice to the same yes choice, per the oracle text). The sacrifice is a delayed
 * trigger that fires at the next end step on [EffectTarget.Self].
 */
val AttackInTheBox = card("Attack-in-the-Box") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Toy"
    power = 2
    toughness = 4
    oracleText = "Whenever this creature attacks, you may have it get +4/+0 until end of turn. " +
        "If you do, sacrifice it at the beginning of the next end step."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = MayEffect(
            Effects.Composite(
                Effects.ModifyStats(4, 0, EffectTarget.Self),
                CreateDelayedTriggerEffect(
                    step = Step.END,
                    effect = Effects.SacrificeTarget(EffectTarget.Self),
                ),
            ),
            descriptionOverride = "Have Attack-in-the-Box get +4/+0 until end of turn? " +
                "(If you do, sacrifice it at the beginning of the next end step.)",
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "242"
        artist = "Domenico Cava"
        flavorText = "Pop goes the evil."
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a477dc3e-0fa1-4ce4-b3de-8cae0d1a0763.jpg?1726286774"
    }
}
