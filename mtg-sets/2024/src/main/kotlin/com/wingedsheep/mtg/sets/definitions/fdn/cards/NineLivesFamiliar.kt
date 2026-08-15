package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

private val revivalCounters = CounterTypeFilter.Named(Counters.REVIVAL)

/**
 * Nine-Lives Familiar
 * {1}{B}{B}
 * Creature — Cat
 * 1/1
 * This creature enters with eight revival counters on it if you cast it.
 * When this creature dies, if it had a revival counter on it, return it to the battlefield with
 * one fewer revival counter on it at the beginning of the next end step.
 *
 * Modeling notes:
 *  - "Enters with … if you cast it" is a replacement effect (CR 614.1c), not an ETB trigger, so a
 *    reanimated or "put onto the battlefield" Familiar simply enters with no counters and never
 *    comes back. [Conditions.WasCast] is the same gate the Sunderflock family uses.
 *  - The dies trigger's intervening "if" reads the *last-known* revival count (CR 603.10a) — the
 *    counters ceased to exist the moment it left the battlefield (CR 122.2). Naming the counter
 *    kind matters: an unrelated +1/+1 counter must not keep the loop alive.
 *  - The return is a delayed trigger at the next end step, on *any* player's turn (the oracle says
 *    "the next end step", not "your next end step"), so it uses the default timing with no
 *    `fireOnPlayer` gate. The count is snapshotted when the delayed trigger is scheduled, because
 *    the last-known-counter context belongs to the dies trigger and is gone by the time it fires.
 *  - `fromZone = Zone.GRAVEYARD` makes the return a no-op if something else moved the card out of
 *    the graveyard in the meantime.
 */
val NineLivesFamiliar = card("Nine-Lives Familiar") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Cat"
    power = 1
    toughness = 1
    oracleText = "This creature enters with eight revival counters on it if you cast it.\n" +
        "When this creature dies, if it had a revival counter on it, return it to the battlefield " +
        "with one fewer revival counter on it at the beginning of the next end step."

    replacementEffect(
        EntersWithCounters(
            counterType = revivalCounters,
            count = 8,
            selfOnly = true,
            condition = Conditions.WasCast
        )
    )

    triggeredAbility {
        trigger = Triggers.Dies
        interveningIf = Compare(
            DynamicAmounts.lastKnownSourceCounters(revivalCounters),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(1)
        )
        effect = CreateDelayedTriggerEffect(
            step = Step.END,
            effect = Effects.Composite(
                Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD),
                Effects.AddDynamicCounters(
                    counterType = Counters.REVIVAL,
                    amount = DynamicAmount.Subtract(
                        DynamicAmounts.lastKnownSourceCounters(revivalCounters),
                        DynamicAmount.Fixed(1)
                    ),
                    target = EffectTarget.Self
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "66"
        artist = "Bram Sels"
        flavorText = "Her master gathered the bones to resurrect her, only to find her purring on the altar."
        imageUri = "https://cards.scryfall.io/normal/front/9/8/988c23f6-59fe-49f9-a9ce-9881dccb7033.jpg?1783909109"
    }
}
