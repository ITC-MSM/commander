package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.TurnTracker

/**
 * War Elemental — Mirrodin #112
 * {R}{R}{R} · Creature — Elemental · 1/1
 *
 * When this creature enters, sacrifice it unless an opponent was dealt damage this turn.
 * Whenever an opponent is dealt damage, put that many +1/+1 counters on this creature.
 *
 * The entry trigger checks opponents' accumulated damage when it resolves; it is not an
 * intervening-if trigger. The second ability observes every damage source and reads the amount
 * actually dealt from the trigger context.
 */
val WarElemental = card("War Elemental") {
    manaCost = "{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    oracleText = "When this creature enters, sacrifice it unless an opponent was dealt damage this turn.\n" +
        "Whenever an opponent is dealt damage, put that many +1/+1 counters on this creature."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ConditionalEffect(
            condition = Conditions.CompareAmounts(
                DynamicAmount.TurnTracking(Player.EachOpponent, TurnTracker.DAMAGE_RECEIVED),
                ComparisonOperator.LT,
                DynamicAmount.Fixed(1),
            ),
            effect = Effects.SacrificeTarget(EffectTarget.Self),
        )
    }

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Any,
            recipient = RecipientFilter.Opponent,
            binding = TriggerBinding.ANY,
        )
        effect = Effects.AddDynamicCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            amount = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            target = EffectTarget.Self,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "112"
        artist = "Anthony S. Waters"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9cc32bfc-7d87-46d9-a424-eac64eefd7ea.jpg?1783944535"
    }
}
