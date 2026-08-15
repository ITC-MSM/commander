package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Diamond Valley
 * Land
 * {T}, Sacrifice a creature: You gain life equal to the sacrificed creature's toughness.
 */
val DiamondValley = card("Diamond Valley") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}, Sacrifice a creature: You gain life equal to the sacrificed creature's toughness."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.Sacrifice(GameObjectFilter.Creature))
        effect = Effects.GainLife(DynamicAmounts.sacrificedToughness(), EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "73"
        artist = "Brian Snõddy"
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e85f6f21-15a0-4a36-be95-5a0299cd01a5.jpg?1761692888"
    }
}
