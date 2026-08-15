package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EventPattern.StepEvent
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.GrantTriggeredAbilityEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Terminal Velocity {4}{R}{R}
 * Sorcery
 *
 * You may put an artifact or creature card from your hand onto the battlefield. That
 * permanent gains haste, "When this permanent leaves the battlefield, it deals damage
 * equal to its mana value to each creature," and "At the beginning of your end step,
 * sacrifice this permanent."
 *
 * The two quoted clauses are granted as real [TriggeredAbility]s on the chosen permanent
 * (Duration.Permanent), not as delayed triggers anchored to the resolving sorcery. That
 * keeps them rules-faithful: the LTB damage reads the permanent's last-known mana value
 * via [EntityReference.Source], and the end-step sacrifice fires every "your end step"
 * for as long as the permanent persists (e.g. if the sacrifice trigger is countered, the
 * permanent keeps all three granted abilities — including the LTB clause).
 */
val TerminalVelocity = card("Terminal Velocity") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "You may put an artifact or creature card from your hand onto the battlefield. " +
        "That permanent gains haste, \"When this permanent leaves the battlefield, it deals damage " +
        "equal to its mana value to each creature,\" and \"At the beginning of your end step, " +
        "sacrifice this permanent.\""

    spell {
        val ltbDamage = TriggeredAbility.create(
            trigger = ZoneChangeEvent(from = Zone.BATTLEFIELD),
            binding = TriggerBinding.SELF,
            effect = Effects.ForEachInGroup(
                filter = GroupFilter.AllCreatures,
                effect = DealDamageEffect(
                    amount = DynamicAmount.EntityProperty(
                        entity = EntityReference.Source,
                        numericProperty = EntityNumericProperty.ManaValue,
                    ),
                    target = EffectTarget.Self,
                ),
            ),
            descriptionOverride = "When this permanent leaves the battlefield, it deals damage equal to its mana value to each creature.",
        )

        val endStepSacrifice = TriggeredAbility.create(
            trigger = StepEvent(Step.END, Player.You),
            binding = TriggerBinding.ANY,
            effect = SacrificeTargetEffect(target = EffectTarget.Self),
            descriptionOverride = "At the beginning of your end step, sacrifice this permanent.",
        )

        val grantClause = ConditionalOnCollectionEffect(
            collection = "putting",
            ifNotEmpty = Effects.Composite(
                Effects.GrantKeyword(
                    keyword = Keyword.HASTE,
                    target = EffectTarget.PipelineTarget("putting", 0),
                    duration = Duration.Permanent,
                ),
                GrantTriggeredAbilityEffect(
                    ability = ltbDamage,
                    target = EffectTarget.PipelineTarget("putting", 0),
                    duration = Duration.Permanent,
                ),
                GrantTriggeredAbilityEffect(
                    ability = endStepSacrifice,
                    target = EffectTarget.PipelineTarget("putting", 0),
                    duration = Duration.Permanent,
                ),
            ),
        )

        effect = Patterns.Hand.putFromHand(
            filter = GameObjectFilter.Artifact or GameObjectFilter.Creature,
        ) then grantClause
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "163"
        artist = "Xabi Gaztelua"
        flavorText = "A victory was the best retirement gift the admiral could have asked for."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d18dc06-16f0-4a3b-8d52-dbf4aa2c393d.jpg?1752947211"
        ruling("2025-07-25", "Use the permanent's mana value as it last existed on the battlefield to determine how much damage the triggered ability deals.")
        ruling("2025-07-25", "If a permanent has {X} in its mana cost, X is 0 for the purpose of determining its mana value.")
    }
}
