package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedTriggeredAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.Aggregation
import com.wingedsheep.sdk.scripting.values.CardNumericProperty
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Case of the Trampled Garden — Murders at Karlov Manor #156
 * {2}{G} · Enchantment — Case · Uncommon
 *
 * When this Case enters, distribute two +1/+1 counters among one or two target creatures you
 * control.
 * To solve — Creatures you control have total power 8 or greater.
 * Solved — Whenever you attack, put a +1/+1 counter on target attacking creature. It gains
 * trample until end of turn.
 *
 * "Among one or two target creatures" is a target *range* — `count = 2, minCount = 1` — with the
 * distribution itself chosen as the spell is put on the stack (CR 601.2d), which is what
 * `DistributeCountersAmongTargets` models; each chosen target must get at least one counter, so
 * choosing two means one each.
 *
 * The "to solve" clause is *total* power, not a creature count, so it is a `SUM` aggregation over
 * power rather than a `COUNT` — one 8/8 solves it as readily as eight 1/1s. It reads projected
 * power, so an anthem or the Case's own counters count toward it.
 *
 * "Whenever you attack" fires once per combat in which you declared at least one attacker, not
 * once per attacker, and its target is chosen when the trigger goes on the stack — after attackers
 * are declared, so any attacking creature is a legal choice.
 */
val CaseOfTheTrampledGarden = card("Case of the Trampled Garden") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Case"
    oracleText = "When this Case enters, distribute two +1/+1 counters among one or two target " +
        "creatures you control.\n" +
        "To solve — Creatures you control have total power 8 or greater. (If unsolved, solve at " +
        "the beginning of your end step.)\n" +
        "Solved — Whenever you attack, put a +1/+1 counter on target attacking creature. It gains " +
        "trample until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = TargetCreature(
            count = 2,
            minCount = 1,
            filter = TargetFilter.Creature.youControl()
        )
        effect = Effects.DistributeCountersAmongTargets(2, Counters.PLUS_ONE_PLUS_ONE)
    }

    toSolve(
        Compare(
            DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Creature,
                aggregation = Aggregation.SUM,
                property = CardNumericProperty.POWER
            ),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(8)
        )
    )

    solvedTriggeredAbility {
        trigger = Triggers.YouAttack
        target = TargetCreature(filter = TargetFilter.Creature.attacking())
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0)),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.ContextTarget(0))
        )
        description = "Solved — Whenever you attack, put a +1/+1 counter on target attacking " +
            "creature. It gains trample until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "156"
        artist = "Maxime Minard"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e80f5c7-ae29-473c-ac64-04bcbc629385.jpg?1783912868"
    }
}
