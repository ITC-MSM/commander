package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.effects.CounterEffect
import com.wingedsheep.sdk.scripting.effects.CounterTargetSource
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect

/**
 * Blood Funnel
 * {1}{B}
 * Enchantment
 * Noncreature spells you cast cost {2} less to cast.
 * Whenever you cast a noncreature spell, counter that spell unless you sacrifice a creature.
 *
 * A discount you pay for in bodies. The two halves are independent: the reduction is a plain
 * [ModifySpellCost] on noncreature spells you cast, and the tax is the punisher shape —
 * [PayOrSufferEffect] with a sacrifice cost and, as the *suffer* branch, a [CounterEffect] aimed
 * at [CounterTargetSource.TriggeringEntity] (the spell that just triggered it).
 *
 * `PayOrSufferEffect` checks affordability before prompting, so a controller with no creature
 * isn't offered an impossible "sacrifice?" — their spell is simply countered. Note that the
 * trigger fires on *every* noncreature spell you cast, discounted or not: casting one with the
 * Funnel out always demands a creature.
 *
 * The trigger resolves above the spell that caused it, so the sacrifice happens (and the spell
 * dies) before that spell would resolve.
 */
val BloodFunnel = card("Blood Funnel") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Noncreature spells you cast cost {2} less to cast.\n" +
        "Whenever you cast a noncreature spell, counter that spell unless you sacrifice a creature."

    // "Noncreature spells you cast cost {2} less to cast."
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Noncreature),
            modification = CostModification.ReduceGeneric(2)
        )
    }

    // "Whenever you cast a noncreature spell, counter that spell unless you sacrifice a creature."
    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = PayOrSufferEffect(
            cost = Costs.pay.Sacrifice(GameObjectFilter.Creature),
            suffer = CounterEffect(targetSource = CounterTargetSource.TriggeringEntity),
            consequenceDescription = "counter that spell"
        )
        description = "Whenever you cast a noncreature spell, counter that spell unless you sacrifice a creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "77"
        artist = "Thomas M. Baxa"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f4e8084-4b9e-48e0-bf59-d45543e176f8.jpg?1783943674"
    }
}
