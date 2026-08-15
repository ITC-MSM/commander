package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Armageddon Clock
 * {6}
 * Artifact
 * At the beginning of your upkeep, put a doom counter on this artifact.
 * At the beginning of your draw step, this artifact deals damage equal to the number of doom
 * counters on it to each player.
 * {4}: Remove a doom counter from this artifact. Any player may activate this ability but only
 * during any upkeep step.
 *
 * Composed from existing primitives plus the new passive [Counters.DOOM] counter (the only engine
 * addition). The accrual is a [Triggers.YourUpkeep] trigger that adds one doom counter
 * ([Effects.AddCounters]). The payoff is a [Triggers.YourDrawStep] trigger that deals
 * `countersOnSelf(DOOM)` damage to each player ([EffectTarget.PlayerRef] over [Player.Each]). The
 * {4} ability removes one doom counter and is gated by [ActivationRestriction.All] of
 * [ActivationRestriction.AnyPlayerMay] (every player, not just the controller) and
 * [ActivationRestriction.DuringStep] of [Step.UPKEEP] (CR 602.1b activation instructions: "Any
 * player may activate this ability but only during any upkeep step").
 */
val ArmageddonClock = card("Armageddon Clock") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "At the beginning of your upkeep, put a doom counter on this artifact.\n" +
        "At the beginning of your draw step, this artifact deals damage equal to the number of " +
        "doom counters on it to each player.\n" +
        "{4}: Remove a doom counter from this artifact. Any player may activate this ability but " +
        "only during any upkeep step."

    // At the beginning of your upkeep, put a doom counter on this artifact.
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.AddCounters(Counters.DOOM, 1, EffectTarget.Self)
        description = "At the beginning of your upkeep, put a doom counter on this artifact."
    }

    // At the beginning of your draw step, this artifact deals damage equal to the number of doom
    // counters on it to each player.
    triggeredAbility {
        trigger = Triggers.YourDrawStep
        effect = Effects.DealDamage(
            DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.DOOM)),
            EffectTarget.PlayerRef(Player.Each)
        )
        description = "At the beginning of your draw step, this artifact deals damage equal to the " +
            "number of doom counters on it to each player."
    }

    // {4}: Remove a doom counter from this artifact. Any player may activate this ability but only
    // during any upkeep step.
    activatedAbility {
        cost = Costs.Mana("{4}")
        effect = Effects.RemoveCounters(Counters.DOOM, 1, EffectTarget.Self)
        // Two top-level restrictions (not wrapped in `All`): the any-player-may enumerator keys
        // off a top-level [ActivationRestriction.AnyPlayerMay], and [ActivationRestriction.DuringStep]
        // confines it to any upkeep step.
        restrictions = listOf(
            ActivationRestriction.AnyPlayerMay,
            ActivationRestriction.DuringStep(Step.UPKEEP)
        )
        description = "{4}: Remove a doom counter from this artifact. Any player may activate this " +
            "ability but only during any upkeep step."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "37"
        artist = "Amy Weber"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44a31889-6a8d-450c-a73d-381a7ff28bf9.jpg?1562909201"
    }
}
