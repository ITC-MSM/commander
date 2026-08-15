package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Clockwork Avian
 * {5}
 * Artifact Creature — Bird
 * 0/4
 * Flying
 * This creature enters with four +1/+0 counters on it.
 * At end of combat, if this creature attacked or blocked this combat, remove a +1/+0 counter from it.
 * {X}, {T}: Put up to X +1/+0 counters on this creature. This ability can't cause the total number
 *   of +1/+0 counters on this creature to be greater than four. Activate only during your upkeep.
 *
 * Modeling notes:
 * - The +1/+0 counters are real stat counters (CR 613.4c, layer 7c) — base 0/4 with four +1/+0 counters
 *   makes it a 4/4. They are shed one per combat it fought (the "attacked or blocked this combat"
 *   end-of-combat trigger), and refilled during your upkeep.
 * - "Put up to X, but never above four total" is modeled as putting
 *   `min(X, 4 - current +1/+0 counters)` counters (floored at 0): the player chose X by paying the
 *   {X} cost; the cap clause then bounds the effective placement. The {X},{T} ability is restricted
 *   to your upkeep.
 */
val ClockworkAvian = card("Clockwork Avian") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Bird"
    power = 0
    toughness = 4
    oracleText = "Flying\n" +
        "This creature enters with four +1/+0 counters on it.\n" +
        "At end of combat, if this creature attacked or blocked this combat, remove a +1/+0 counter from it.\n" +
        "{X}, {T}: Put up to X +1/+0 counters on this creature. This ability can't cause the total " +
        "number of +1/+0 counters on this creature to be greater than four. Activate only during your upkeep."

    keywords(Keyword.FLYING)

    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusZero,
        count = 4,
        selfOnly = true
    ))

    triggeredAbility {
        trigger = Triggers.EachEndOfCombat
        triggerCondition = Conditions.SourceAttackedOrBlockedThisCombat
        effect = Effects.RemoveCounters(Counters.PLUS_ONE_PLUS_ZERO, 1, EffectTarget.Self)
        description = "At end of combat, if this creature attacked or blocked this combat, remove a +1/+0 counter from it."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{X}"), Costs.Tap)
        // Put up to X +1/+0 counters, never raising the total above four:
        // min(X, 4 - current), floored at 0.
        effect = Effects.AddDynamicCounters(
            Counters.PLUS_ONE_PLUS_ZERO,
            DynamicAmount.IfPositive(
                DynamicAmount.Min(
                    DynamicAmount.XValue,
                    DynamicAmount.Subtract(
                        DynamicAmount.Fixed(4),
                        DynamicAmounts.countersOnSelf(CounterTypeFilter.PlusOnePlusZero)
                    )
                )
            ),
            EffectTarget.Self
        )
        restrictions = listOf(
            ActivationRestriction.DuringStep(Step.UPKEEP),
            ActivationRestriction.OnlyDuringYourTurn
        )
        description = "{X}, {T}: Put up to X +1/+0 counters on this creature (max four total). Activate only during your upkeep."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "45"
        artist = "Randy Asplund-Faith"
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1dea8c2f-4aea-478d-aee7-cba1f74edd6c.jpg?1562901278"
    }
}
