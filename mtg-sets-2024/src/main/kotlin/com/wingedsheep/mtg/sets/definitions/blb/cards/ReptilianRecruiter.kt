package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.AnyCondition
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Reptilian Recruiter {3}{R}{R}
 * Creature — Lizard Warrior
 * 4/2
 *
 * Trample
 * When this creature enters, choose target creature. If that creature's power is 2 or less
 * or if you control another Lizard, gain control of that creature until end of turn,
 * untap it, and it gains haste until end of turn.
 */
val ReptilianRecruiter = card("Reptilian Recruiter") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard Warrior"
    power = 4
    toughness = 2
    oracleText = "Trample\nWhen this creature enters, choose target creature. If that creature's power is 2 or less or if you control another Lizard, gain control of that creature until end of turn, untap it, and it gains haste until end of turn."

    keywords(Keyword.TRAMPLE)

    // ETB: conditional Threaten
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("creature", Targets.Creature)
        effect = ConditionalEffect(
            condition = AnyCondition(
                listOf(
                    // Target creature's power is 2 or less
                    Compare(
                        DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.Power),
                        ComparisonOperator.LTE,
                        DynamicAmount.Fixed(2)
                    ),
                    // You control another Lizard (2+ Lizards total since this is a Lizard)
                    Compare(
                        DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.withSubtype("Lizard")),
                        ComparisonOperator.GTE,
                        DynamicAmount.Fixed(2)
                    )
                )
            ),
            effect = Effects.GainControl(creature, Duration.EndOfTurn)
                .then(Effects.Untap(creature))
                .then(Effects.GrantKeyword(Keyword.HASTE, creature))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "149"
        artist = "Joshua Cairos"
        imageUri = "https://cards.scryfall.io/normal/front/8/1/81dec453-c9d7-42cb-980a-c82f82bede76.jpg?1721426690"
        ruling("2024-07-26", "You can target any creature with Reptilian Recruiter's triggered ability. Whether or not the ability does anything when it resolves will depend on whether the condition is met at that time.")
    }
}
