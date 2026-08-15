package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Powerstone Shard
 * {3}
 * Artifact
 * {T}: Add {C} for each artifact you control named Powerstone Shard.
 */
val PowerstoneShard = card("Powerstone Shard") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}: Add {C} for each artifact you control named Powerstone Shard."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(
            DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Artifact.named("Powerstone Shard")
            )
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "227"
        artist = "Lindsey Look"
        flavorText = "\"Light passing through a powerstone is refracted by eternity and colored by planar energy. I wonder how the world appeared through Urza's eyes?\"\n—Teferi"
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d62fd366-c287-48c3-9ded-5dc63a34518c.jpg?1562743673"
    }
}
