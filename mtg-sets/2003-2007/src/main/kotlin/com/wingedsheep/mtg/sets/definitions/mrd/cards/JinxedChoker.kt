package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ChooseActionEffect
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.EffectChoice
import com.wingedsheep.sdk.scripting.effects.GiveControlToTargetPlayerEffect
import com.wingedsheep.sdk.scripting.effects.RemoveCountersEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetOpponent

/**
 * Jinxed Choker — Mirrodin #189
 * {3} · Artifact
 *
 * At the beginning of your end step, target opponent gains control of this artifact and puts a
 * charge counter on it.
 * At the beginning of your upkeep, this artifact deals damage to you equal to the number of charge
 * counters on it.
 * {3}: Put a charge counter on this artifact or remove one from it.
 *
 * The activated ability chooses its action on resolution, as the card's ruling requires. Removing
 * is only offered while a charge counter exists; otherwise adding is the sole legal choice.
 */
val JinxedChoker = card("Jinxed Choker") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "At the beginning of your end step, target opponent gains control of this artifact " +
        "and puts a charge counter on it.\n" +
        "At the beginning of your upkeep, this artifact deals damage to you equal to the number of " +
        "charge counters on it.\n" +
        "{3}: Put a charge counter on this artifact or remove one from it."

    triggeredAbility {
        trigger = Triggers.YourEndStep
        val opponent = target("opponent", TargetOpponent())
        effect = Effects.Composite(
            GiveControlToTargetPlayerEffect(
                permanent = EffectTarget.Self,
                newController = opponent,
            ),
            Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self),
        )
    }

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.DealDamage(
            amount = DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.CHARGE)),
            target = EffectTarget.Controller,
        )
    }

    activatedAbility {
        cost = Costs.Mana("{3}")
        val addCounter = Effects.AddCounters(Counters.CHARGE, 1, EffectTarget.Self)
        effect = ConditionalEffect(
            condition = Conditions.SourceHasCounter(CounterTypeFilter.Named(Counters.CHARGE)),
            effect = ChooseActionEffect(
                choices = listOf(
                    EffectChoice("Put a charge counter on Jinxed Choker", addCounter),
                    EffectChoice(
                        "Remove a charge counter from Jinxed Choker",
                        RemoveCountersEffect(Counters.CHARGE, 1, EffectTarget.Self),
                    ),
                ),
            ),
            elseEffect = addCounter,
        )
        description = "Put a charge counter on this artifact or remove one from it"
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "189"
        artist = "Mike Dringenberg"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/987910b0-0419-45ff-bda6-c6683fd00e49.jpg?1783944517"
        ruling("2004-12-01", "“You” is always Jinxed Choker’s current controller.")
        ruling("2004-12-01", "If you activate Jinxed Choker’s activated ability, you choose to either add or remove a counter when the ability resolves.")
    }
}
