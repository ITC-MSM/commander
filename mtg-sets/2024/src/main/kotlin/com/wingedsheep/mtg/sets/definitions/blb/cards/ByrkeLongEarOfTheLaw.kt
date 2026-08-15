package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Byrke, Long Ear of the Law
 * {4}{G}{W}
 * Legendary Creature — Rabbit Soldier
 * 4/4
 *
 * Vigilance
 * When Byrke enters, put a +1/+1 counter on each of up to two target creatures.
 * Whenever a creature you control with a +1/+1 counter on it attacks, double the
 * number of +1/+1 counters on it.
 *
 * The ETB mirrors Weftblade Enhancer's "each of up to two target creatures" shape —
 * an optional `count = 2` [TargetCreature] fanned out with [ForEachTargetEffect] so
 * each chosen creature gets one counter. The attack trigger fires for ANY creature
 * you control that already carries a +1/+1 counter (the `withCounter` filter), then
 * doubles that creature's +1/+1 counters via [Effects.DoubleCounters] on the
 * triggering attacker ("it"). Doubling reads the post-declaration count, so multiple
 * counters from earlier doublings/anthems compound, and Branching Evolution-style
 * placement replacements still apply to the freshly placed counters (per ruling).
 */
val ByrkeLongEarOfTheLaw = card("Byrke, Long Ear of the Law") {
    manaCost = "{4}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Rabbit Soldier"
    power = 4
    toughness = 4
    oracleText = "Vigilance\n" +
        "When Byrke enters, put a +1/+1 counter on each of up to two target creatures.\n" +
        "Whenever a creature you control with a +1/+1 counter on it attacks, double the " +
        "number of +1/+1 counters on it."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("up to two target creatures", TargetCreature(count = 2, optional = true))
        effect = ForEachTargetEffect(
            listOf(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0)))
        )
        description = "When Byrke enters, put a +1/+1 counter on each of up to two target creatures."
    }

    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.youControl().withCounter(Counters.PLUS_ONE_PLUS_ONE),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.DoubleCounters(Counters.PLUS_ONE_PLUS_ONE, EffectTarget.TriggeringEntity)
        description = "Whenever a creature you control with a +1/+1 counter on it attacks, " +
            "double the number of +1/+1 counters on it."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "380"
        artist = "Manuel Castañón"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6441abd3-320b-424a-9753-61e3581fe1a9.jpg?1726396879"
        ruling(
            "2024-07-26",
            "To double the number of +1/+1 counters on a creature, put a number of +1/+1 counters " +
                "on it equal to the number it already has. Replacement effects that modify the number " +
                "of counters being placed on creatures you control, such as the effect of Branching " +
                "Evolution, apply to this ability as normal.",
        )
    }
}
