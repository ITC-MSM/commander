package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Turtle Van
 * {3}
 * Artifact — Vehicle
 * 4/4
 *
 * Whenever this Vehicle attacks, put a +1/+1 counter on target creature that
 * crewed it this turn. Then if that creature is a Mutant, Ninja, or Turtle,
 * double the number of +1/+1 counters on it.
 * Crew 1
 */
val TurtleVan = card("Turtle Van") {
    manaCost = "{3}"
    typeLine = "Artifact — Vehicle"
    oracleText = "Whenever this Vehicle attacks, put a +1/+1 counter on target creature that crewed it this turn. Then if that creature is a Mutant, Ninja, or Turtle, double the number of +1/+1 counters on it.\nCrew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.Attacks
        val crewer = target(
            "target creature that crewed it this turn",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.crewedOrSaddledSourceThisTurn()))
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, crewer)
            .then(
                ConditionalEffect(
                    condition = Conditions.TargetMatchesFilter(
                        GameObjectFilter.Creature.withAnyOfSubtypes(
                            listOf(Subtype("Mutant"), Subtype("Ninja"), Subtype("Turtle"))
                        )
                    ),
                    effect = Effects.DoubleCounters(Counters.PLUS_ONE_PLUS_ONE, crewer)
                )
            )
        description = "Whenever this Vehicle attacks, put a +1/+1 counter on target creature that crewed it this turn. Then if that creature is a Mutant, Ninja, or Turtle, double the number of +1/+1 counters on it."
    }

    keywordAbility(KeywordAbility.Numeric(Keyword.CREW, 1))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "181"
        artist = "Jakob Eirich"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fce6a8b6-b43c-4045-9378-97b2463f9b4d.jpg?1769006474"
    }
}
