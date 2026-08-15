package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Chrome Dome
 * {2}
 * Artifact Creature — Robot Ninja
 * 1/3
 *
 * Other artifact creatures you control get +1/+0.
 * {5}: Create a token that's a copy of another target artifact you control. That
 * token gains haste. Sacrifice it at the beginning of the next end step.
 */
val ChromeDome = card("Chrome Dome") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Robot Ninja"
    oracleText = "Other artifact creatures you control get +1/+0.\n{5}: Create a token that's a copy of another target artifact you control. That token gains haste. Sacrifice it at the beginning of the next end step."
    power = 1
    toughness = 3

    val otherArtifactCreatures = GroupFilter(
        GameObjectFilter(
            cardPredicates = listOf(CardPredicate.IsCreature, CardPredicate.IsArtifact)
        ).youControl(),
        excludeSelf = true
    )

    staticAbility {
        ability = ModifyStats(1, 0, otherArtifactCreatures)
    }

    activatedAbility {
        val artifact = target(
            "another target artifact you control",
            TargetObject(filter = TargetFilter(GameObjectFilter.Artifact.youControl(), excludeSelf = true))
        )
        cost = Costs.Mana("{5}")
        effect = Effects.CreateTokenCopyOfTarget(
            target = artifact,
            addedKeywords = setOf(Keyword.HASTE),
            sacrificeAtStep = Step.END
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "172"
        artist = "Mathias Kollros"
        flavorText = "\"Broadcast: Seized. Initiate seek-and-destroy protocols.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/9/994a01eb-2689-49e7-be23-0713da9da9a8.jpg?1769006406"
    }
}
