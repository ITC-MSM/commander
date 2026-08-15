package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Does Machines
 * {1}{U}
 * Enchantment — Class
 *
 * (Gain the next level as a sorcery to add its ability.)
 * When this Class enters, mill two cards, draw two cards, then discard two cards.
 * {1}{U}: Level 2
 * When this Class becomes level 2, return up to two target artifact cards from your graveyard to your hand.
 * {4}{U}: Level 3
 * At the beginning of combat on your turn, put three +1/+1 counters on target artifact
 * you control. If it isn't a creature, it becomes a 0/0 Robot creature in addition to its
 * other types.
 */
val DoesMachines = card("Does Machines") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Class"
    oracleText = "(Gain the next level as a sorcery to add its ability.)\n" +
        "When this Class enters, mill two cards, draw two cards, then discard two cards.\n" +
        "{1}{U}: Level 2\nWhen this Class becomes level 2, return up to two target artifact cards from your graveyard to your hand.\n" +
        "{4}{U}: Level 3\nAt the beginning of combat on your turn, put three +1/+1 counters on target artifact you control. If it isn't a creature, it becomes a 0/0 Robot creature in addition to its other types."

    // Level 1: ETB mill 2, draw 2, then discard 2.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.mill(2)
            .then(Effects.DrawCards(2))
            .then(Patterns.Hand.discardCards(2))
    }

    // Level 2: "When this Class becomes level 2" — modeled as an EntersBattlefield trigger
    // inside the level block (Caretaker's Talent idiom).
    classLevel(2, "{1}{U}") {
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            target = TargetObject(
                count = 2,
                optional = true,
                filter = TargetFilter(GameObjectFilter.Artifact.ownedByYou(), zone = Zone.GRAVEYARD)
            )
            effect = ForEachTargetEffect(
                effects = listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND))
            )
        }
    }

    // Level 3: begin combat on your turn — three +1/+1 counters on a target artifact you
    // control; a noncreature one also becomes a 0/0 Robot in addition to its other types.
    classLevel(3, "{4}{U}") {
        triggeredAbility {
            trigger = Triggers.BeginCombat
            target = TargetObject(
                count = 1,
                filter = TargetFilter(GameObjectFilter.Artifact.youControl())
            )
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, EffectTarget.ContextTarget(0))
                .then(
                    ConditionalEffect(
                        condition = Conditions.Not(Conditions.TargetMatchesFilter(GameObjectFilter.Creature)),
                        effect = Effects.BecomeCreature(
                            target = EffectTarget.ContextTarget(0),
                            power = 0,
                            toughness = 0,
                            creatureTypes = setOf("Robot"),
                            duration = Duration.Permanent
                        )
                    )
                )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "34"
        artist = "Aeron Ng"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/989da63a-2cbd-41a9-9bbb-99f4ad1c6a25.jpg?1774102268"
    }
}
