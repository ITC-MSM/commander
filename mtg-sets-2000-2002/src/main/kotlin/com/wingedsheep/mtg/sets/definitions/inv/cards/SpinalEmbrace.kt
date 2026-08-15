package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spinal Embrace
 * {3}{U}{U}{B}
 * Instant
 * Cast this spell only during combat.
 * Untap target creature you don't control and gain control of it. It gains haste until
 * end of turn. At the beginning of the next end step, sacrifice it. If you do, you gain
 * life equal to its toughness.
 *
 * The delayed end-step trigger sacrifices the stolen creature and gains life equal to its
 * toughness as it last existed on the battlefield (Rule 608.2h). The "if you do" clause
 * falls out naturally: when the creature is no longer present, SacrificeTarget does nothing,
 * records no snapshot, and the following GainLife reads no sacrificed permanent (= 0 life).
 */
val SpinalEmbrace = card("Spinal Embrace") {
    manaCost = "{3}{U}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Instant"
    oracleText = "Cast this spell only during combat.\n" +
        "Untap target creature you don't control and gain control of it. It gains haste " +
        "until end of turn. At the beginning of the next end step, sacrifice it. If you do, " +
        "you gain life equal to its toughness."

    spell {
        castOnlyDuring(Phase.COMBAT)
        val t = target("target creature you don't control", Targets.CreatureOpponentControls)
        effect = Effects.Composite(
            Effects.Untap(t),
            Effects.GainControl(t, Duration.Permanent),
            Effects.GrantKeyword(Keyword.HASTE, t, Duration.EndOfTurn),
            CreateDelayedTriggerEffect(
                step = Step.END,
                effect = Effects.Composite(
                    Effects.SacrificeTarget(t),
                    Effects.GainLife(DynamicAmounts.sacrificedToughness(), EffectTarget.Controller)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "276"
        artist = "Donato Giancola"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/692ad1eb-62a3-4560-bf8e-35f7db73c7a3.jpg?1562916171"
    }
}
