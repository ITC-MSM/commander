package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Blazing Bomb
 * {R}
 * Creature — Elemental
 * 1/1
 * Whenever you cast a noncreature spell, if at least four mana was spent to cast it, put a +1/+1
 * counter on this creature.
 * Blow Up — {T}, Sacrifice this creature: It deals damage equal to its power to target creature.
 * Activate only as a sorcery.
 *
 * The cast trigger uses an intervening-if (CR 603.4) comparing the *triggering* spell's mana spent
 * (`ContextProperty(MANA_SPENT_ON_TRIGGERING_SPELL)`) to 4 — distinct from `TotalManaSpent`, which
 * reads the resolving object's own cast.
 *
 * Blow Up sacrifices the source as part of its cost, so the creature is gone by the time the ability
 * resolves. `DynamicAmounts.sourcePower()` reads the source's **last-known** power (captured at
 * cost-payment time, CR 112.7a / 608.2h), so a buffed Bomb deals its accumulated power rather than 0.
 */
val BlazingBomb = card("Blazing Bomb") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 1
    toughness = 1
    oracleText = "Whenever you cast a noncreature spell, if at least four mana was spent to cast " +
        "it, put a +1/+1 counter on this creature.\n" +
        "Blow Up — {T}, Sacrifice this creature: It deals damage equal to its power to target " +
        "creature. Activate only as a sorcery."

    // Whenever you cast a noncreature spell, if at least four mana was spent to cast it,
    // put a +1/+1 counter on this creature.
    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        triggerCondition = Conditions.CompareAmounts(
            DynamicAmount.ContextProperty(ContextPropertyKey.MANA_SPENT_ON_TRIGGERING_SPELL),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(4),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    // Blow Up — {T}, Sacrifice this creature: It deals damage equal to its power to target
    // creature. Activate only as a sorcery.
    activatedAbility {
        description = "Blow Up"
        cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf)
        timing = TimingRule.SorcerySpeed
        val victim = target("target", Targets.Creature)
        effect = Effects.DealDamage(DynamicAmounts.sourcePower(), victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Andrea Radeck"
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70f47277-ca47-428a-808f-0fb32e820a71.jpg?1748706252"
    }
}
