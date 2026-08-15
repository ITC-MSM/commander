package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Desert
 * Land — Desert
 * {T}: Add {C}.
 * {T}: This land deals 1 damage to target attacking creature. Activate only during the end of combat step.
 */
val Desert = card("Desert") {
    typeLine = "Land — Desert"
    colorIdentity = ""
    oracleText = "{T}: Add {C}.\n{T}: This land deals 1 damage to target attacking creature. Activate only during the end of combat step."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        val creature = target(
            "target attacking creature",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.attacking()))
        )
        effect = Effects.DealDamage(1, creature)
        restrictions = listOf(ActivationRestriction.DuringStep(Step.END_COMBAT))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Jesper Myrfors"
        imageUri = "https://cards.scryfall.io/normal/front/2/0/201155ea-f474-4e13-acda-cb071a6ca977.jpg?1562900934"
    }
}
