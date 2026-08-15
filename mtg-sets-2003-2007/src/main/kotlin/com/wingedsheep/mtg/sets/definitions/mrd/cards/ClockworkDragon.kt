package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Clockwork Dragon — Mirrodin #155
 * {7} · Artifact Creature — Dragon · 0/0 · Rare
 *
 * Flying
 * This creature enters with six +1/+1 counters on it.
 * Whenever this creature attacks or blocks, remove a +1/+1 counter from it at end of combat.
 * {3}: Put a +1/+1 counter on this creature.
 *
 * The top of the Mirrodin Clockwork ladder, and modelled exactly like its setmates Clockwork Beetle
 * and Clockwork Condor (and their Antiquities ancestor Clockwork Avian): printed 0/0 plus six
 * [EntersWithCounters] +1/+1 counters (CR 613.4c, layer 7d) make a 6/6 on the battlefield, and it
 * dies as a state-based action once the last counter is shed.
 *
 * The counter-shed line is a trigger that *sets up a delayed trigger* ("…remove a counter from it at
 * end of combat"). [Triggers.EachEndOfCombat] with the intervening-if
 * [Conditions.SourceAttackedOrBlockedThisCombat] is observationally identical — one counter shed per
 * combat the Dragon fought in, on any player's turn — because the delayed trigger and the tracker
 * are keyed to the same object and both go away when it leaves the battlefield.
 *
 * Unlike Clockwork Avian, the recharge ability carries **no** timing or ceiling restriction: it is a
 * plain `{3}` activation usable any time the Dragon's controller has priority, and it can push the
 * Dragon above six counters.
 */
val ClockworkDragon = card("Clockwork Dragon") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Dragon"
    power = 0
    toughness = 0
    oracleText = "Flying\n" +
        "This creature enters with six +1/+1 counters on it.\n" +
        "Whenever this creature attacks or blocks, remove a +1/+1 counter from it at end of combat.\n" +
        "{3}: Put a +1/+1 counter on this creature."

    keywords(Keyword.FLYING)

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.PlusOnePlusOne,
            count = 6,
            selfOnly = true
        )
    )

    triggeredAbility {
        trigger = Triggers.EachEndOfCombat
        triggerCondition = Conditions.SourceAttackedOrBlockedThisCombat
        effect = Effects.RemoveCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever this creature attacks or blocks, remove a +1/+1 counter from it at end of combat."
    }

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "{3}: Put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "155"
        artist = "Arnie Swekel"
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca6448da-b513-4bea-95f4-47bdfa0df078.jpg?1783944525"
    }
}
