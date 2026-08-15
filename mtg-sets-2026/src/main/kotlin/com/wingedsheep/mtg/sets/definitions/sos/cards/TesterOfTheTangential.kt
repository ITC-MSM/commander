package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.increment
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayXForEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Tester of the Tangential — Secrets of Strixhaven #69
 * {1}{U} · Creature — Djinn Wizard · 1/1
 *
 * Increment (Whenever you cast a spell, if the amount of mana you spent is greater than this
 * creature's power or toughness, put a +1/+1 counter on this creature.)
 * At the beginning of combat on your turn, you may pay {X}. When you do, move X +1/+1 counters
 * from this creature onto another target creature.
 *
 * The second ability is a may-pay-{X} reflexive: [MayPayXForEffect] pauses for the X chooser and
 * binds the chosen X into [DynamicAmount.XValue], then resolves its inner pipeline — select
 * "another target creature", then [Effects.MoveCounters] moves X +1/+1 counters from this creature
 * (capped at the number present) onto the selected target. Targeting happens after the payment, as
 * the "When you do" reflexive prescribes.
 */
val TesterOfTheTangential = card("Tester of the Tangential") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Djinn Wizard"
    power = 1
    toughness = 1
    oracleText = "Increment (Whenever you cast a spell, if the amount of mana you spent is greater " +
        "than this creature's power or toughness, put a +1/+1 counter on this creature.)\n" +
        "At the beginning of combat on your turn, you may pay {X}. When you do, move X +1/+1 " +
        "counters from this creature onto another target creature."

    increment()

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = MayPayXForEffect(
            effect = Effects.SelectTarget(TargetCreature(filter = TargetFilter.OtherCreature), "moveTarget")
                .then(
                    Effects.MoveCounters(
                        counterType = Counters.PLUS_ONE_PLUS_ONE,
                        amount = DynamicAmount.XValue,
                        source = EffectTarget.Self,
                        destination = EffectTarget.PipelineTarget("moveTarget"),
                    )
                )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "69"
        artist = "Lorenzo Mastroianni"
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bbd708ec-eef4-4f45-99dd-60e1cec4b991.jpg?1775937389"
    }
}
