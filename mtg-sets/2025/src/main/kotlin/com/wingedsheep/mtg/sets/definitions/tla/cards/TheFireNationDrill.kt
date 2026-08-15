package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * The Fire Nation Drill
 * {2}{B}{B}
 * Legendary Artifact — Vehicle
 * 6/3
 *
 * Trample
 * When The Fire Nation Drill enters, you may tap it. When you do, destroy
 * target creature with power 4 or less.
 * {1}: Permanents your opponents control lose hexproof and indestructible
 * until end of turn.
 * Crew 2
 */
val TheFireNationDrill = card("The Fire Nation Drill") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Artifact — Vehicle"
    oracleText = "Trample\n" +
        "When The Fire Nation Drill enters, you may tap it. When you do, destroy target creature with power 4 or less.\n" +
        "{1}: Permanents your opponents control lose hexproof and indestructible until end of turn.\n" +
        "Crew 2"
    power = 6
    toughness = 3

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = Effects.Tap(EffectTarget.Self),
            optional = true,
            reflexiveEffect = Effects.Destroy(EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(Targets.CreatureWithPowerAtMost(4))
        )
    }

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.Composite(
            Patterns.Group.removeKeywordFromAll(
                Keyword.HEXPROOF,
                Filters.Group.permanents { opponentControls() }
            ),
            Patterns.Group.removeKeywordFromAll(
                Keyword.INDESTRUCTIBLE,
                Filters.Group.permanents { opponentControls() }
            )
        )
    }

    keywordAbility(KeywordAbility.crew(2))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "98"
        artist = "Brandon L. Hunt"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54d762f6-e131-480f-b294-10f5a63d9c98.jpg?1764120677"
    }
}
