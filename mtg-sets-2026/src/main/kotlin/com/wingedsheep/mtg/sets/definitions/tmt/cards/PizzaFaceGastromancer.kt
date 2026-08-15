package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Pizza Face, Gastromancer
 * {3}{B}{G}
 * Legendary Artifact Creature — Food Mutant
 * 2/4
 *
 * When Pizza Face enters, create a Food token.
 * Disappear — At the beginning of your end step, if a permanent left the
 * battlefield under your control this turn, put three +1/+1 counters on up to
 * one other target artifact or creature. If it isn't a creature, it becomes a
 * 0/0 Mutant creature in addition to its other types.
 * {10}, {T}, Sacrifice Pizza Face: You gain 15 life.
 */
val PizzaFaceGastromancer = card("Pizza Face, Gastromancer") {
    manaCost = "{3}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Artifact Creature — Food Mutant"
    oracleText = "When Pizza Face enters, create a Food token.\nDisappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, put three +1/+1 counters on up to one other target artifact or creature. If it isn't a creature, it becomes a 0/0 Mutant creature in addition to its other types.\n{10}, {T}, Sacrifice Pizza Face: You gain 15 life."
    power = 2
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood()
        description = "When Pizza Face enters, create a Food token."
    }

    // Disappear — three +1/+1 counters on up to one other target artifact or creature;
    // a noncreature target also becomes a 0/0 Mutant creature in addition to its other
    // types (same conditional-BecomeCreature idiom as Brilliance Unleashed).
    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouHadPermanentLeaveBattlefieldThisTurn
        target = TargetObject(
            count = 1,
            optional = true,
            filter = TargetFilter.CreatureOrArtifact.copy(excludeSelf = true)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, EffectTarget.ContextTarget(0))
            .then(
                ConditionalEffect(
                    condition = Conditions.Not(
                        Conditions.TargetMatchesFilter(GameObjectFilter.Creature)
                    ),
                    effect = Effects.BecomeCreature(
                        target = EffectTarget.ContextTarget(0),
                        power = 0,
                        toughness = 0,
                        creatureTypes = setOf("Mutant"),
                        duration = Duration.Permanent
                    )
                )
            )
        description = "Disappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, put three +1/+1 counters on up to one other target artifact or creature. If it isn't a creature, it becomes a 0/0 Mutant creature in addition to its other types."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{10}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.GainLife(15)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "163"
        artist = "Villarrte"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b03cf0bb-3207-4e8e-bb3f-e3e4367aa86e.jpg?1771599896"
    }
}
