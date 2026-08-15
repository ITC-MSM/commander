package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Second Harvest
 * {2}{G}{G}
 * Instant
 * For each token you control, create a token that's a copy of that permanent.
 *
 * `ForEachInGroup` snapshots the tokens you control at resolution and creates one copy per token
 * ([EffectTarget.Self] = the iterated token); the freshly-minted copies are not re-iterated.
 */
val SecondHarvest = card("Second Harvest") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "For each token you control, create a token that's a copy of that permanent."

    spell {
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Token.youControl()),
            effect = Effects.CreateTokenCopyOfTarget(target = EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "227"
        artist = "Matt Stewart"
        flavorText = "\"The cornfields promise a good yield this season.\"\n—Radwick, farmer of Gatstaf"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d0c5127-cd17-4859-8de8-165c4c748e89.jpg?1782712000"
    }
}
