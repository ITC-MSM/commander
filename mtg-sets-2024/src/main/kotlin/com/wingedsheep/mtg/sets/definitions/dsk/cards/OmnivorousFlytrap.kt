package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachEffect
import com.wingedsheep.sdk.scripting.effects.IterationSpace
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Omnivorous Flytrap
 * {2}{G}
 * Creature — Plant
 * 2/4
 *
 * Delirium — Whenever this creature enters or attacks, if there are four or more card types among
 * cards in your graveyard, distribute two +1/+1 counters among one or two target creatures. Then if
 * there are six or more card types among cards in your graveyard, double the number of +1/+1
 * counters on those creatures.
 *
 * Delirium is an ability word (no rules meaning); both the enters trigger and the attacks trigger
 * carry an intervening-"if" gate of [Conditions.Delirium] (four+ distinct card types in your
 * graveyard) — modeled as two separate triggered abilities (Sentinel of the Nameless City shape).
 *
 * The payoff distributes two +1/+1 counters across one or two *target* creatures
 * (`TargetCreature(count = 2, minCount = 1)` + [Effects.DistributeCountersAmongTargets]). The
 * second clause is a separate check at resolution: gated on six+ card types ([Conditions.Delirium]
 * with count = 6), it doubles the +1/+1 counters on **those same creatures** — iterating the chosen
 * targets via [IterationSpace.Targets] so [Effects.DoubleCounters] applies to each (one or two).
 * Each target received at least one counter from the distribution, so doubling is always meaningful;
 * a target that became illegal before resolution is simply skipped by the iteration.
 */
val OmnivorousFlytrap = card("Omnivorous Flytrap") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant"
    power = 2
    toughness = 4
    oracleText = "Delirium — Whenever this creature enters or attacks, if there are four or more " +
        "card types among cards in your graveyard, distribute two +1/+1 counters among one or two " +
        "target creatures. Then if there are six or more card types among cards in your graveyard, " +
        "double the number of +1/+1 counters on those creatures."

    // Distribute two +1/+1 counters among one or two target creatures, then (delirium ≥ 6) double
    // the +1/+1 counters on those same creatures.
    val payoff = Effects.Composite(
        Effects.DistributeCountersAmongTargets(totalCounters = 2),
        ConditionalEffect(
            condition = Conditions.Delirium(count = 6),
            effect = ForEachEffect(
                space = IterationSpace.Targets,
                body = Effects.DoubleCounters(target = EffectTarget.ContextTarget(0)),
            ),
        ),
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.Delirium()
        target = TargetCreature(count = 2, minCount = 1)
        effect = payoff
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.Delirium()
        target = TargetCreature(count = 2, minCount = 1)
        effect = payoff
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "192"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/4370a197-68cc-46dd-bf0a-e9dab4ff6638.jpg?1726286582"
    }
}
