package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Compare
import com.wingedsheep.sdk.scripting.conditions.ComparisonOperator
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Prairie Stream
 *
 * Land — Plains Island
 * ({T}: Add {W} or {U}.)
 * This land enters tapped unless you control two or more basic lands.
 */
val PrairieStream = card("Prairie Stream") {
    colorIdentity = "WU"
    typeLine = "Land — Plains Island"
    oracleText = "({T}: Add {W} or {U}.)\nThis land enters tapped unless you control two or more basic lands."

    // Mana abilities are intrinsic from the basic land types in the type line.

    replacementEffect(EntersTapped(
        unlessCondition = Compare(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.BasicLand),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(2)
        )
    ))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "241"
        artist = "Adam Paquette"
        flavorText = "The continent of Ondu is a vast plateau crisscrossed by deep trenches and meandering rivers."
        imageUri = "https://cards.scryfall.io/normal/front/9/6/9625911f-1dd3-4044-ac06-3c0c3198b6fd.jpg"
    }
}
