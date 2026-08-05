package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Crowd of True Believers — Marvel Super Heroes #14 (common)
 * {W} · Creature — Human Citizen · 1/2
 *
 * {T}: Target creature you control that's attacking alone gets +1/+0 until end of turn.
 * You gain 1 life.
 *
 * "Attacking alone" is CR 506.5 — *a creature is attacking alone if it's attacking but no other
 * creatures are*. That decomposes into two existing primitives rather than a new predicate:
 *  - the **target** is an attacking creature you control
 *    ([TargetFilter.Creature].youControl().attacking()), and
 *  - the **global** "no other creature is attacking" half is an
 *    [ActivationRestriction.OnlyIfCondition] comparing the battlefield-wide attacker count
 *    ([DynamicAmount.AggregateBattlefield] over [Player.Each], so defending-player attackers in
 *    multiplayer count too) against 1.
 *
 * With exactly one attacker on the battlefield, the target filter can only ever pick that
 * creature, so the pair is equivalent to the printed restriction at activation time. Known
 * limitation: the alone-ness gate is an *activation* restriction, so it is not re-checked on
 * resolution — if a second creature somehow starts attacking with the ability on the stack (an
 * effect that puts a creature onto the battlefield attacking), the ability still resolves. The
 * target itself is re-checked normally, so the ability still fizzles if the creature stops
 * attacking or leaves.
 */
private val ATTACKING_ALONE = Conditions.CompareAmounts(
    DynamicAmount.AggregateBattlefield(Player.Each, GameObjectFilter.Creature.attacking()),
    ComparisonOperator.EQ,
    DynamicAmount.Fixed(1),
)

val CrowdOfTrueBelievers = card("Crowd of True Believers") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Citizen"
    power = 1
    toughness = 2
    oracleText = "{T}: Target creature you control that's attacking alone gets +1/+0 until end " +
        "of turn. You gain 1 life."

    activatedAbility {
        cost = Costs.Tap
        val attacker = target(
            "target creature you control that's attacking alone",
            TargetCreature(filter = TargetFilter.Creature.youControl().attacking()),
        )
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, attacker),
            Effects.GainLife(1),
        )
        restrictions = listOf(ActivationRestriction.OnlyIfCondition(ATTACKING_ALONE))
        description = "{T}: Target creature you control that's attacking alone gets +1/+0 until " +
            "end of turn. You gain 1 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Michele Giorgi"
        flavorText = "Being a hero is easy when you know who you're fighting for."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b2fffb8-d538-4772-8fbc-9bec3b9c4d9c.jpg?1783902974"
    }
}
