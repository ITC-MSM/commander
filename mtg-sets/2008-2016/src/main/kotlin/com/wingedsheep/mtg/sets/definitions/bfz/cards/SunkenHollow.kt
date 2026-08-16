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
 * Sunken Hollow
 *
 * Land — Island Swamp
 * ({T}: Add {U} or {B}.)
 * This land enters tapped unless you control two or more basic lands.
 */
val SunkenHollow = card("Sunken Hollow") {
    colorIdentity = "UB"
    typeLine = "Land — Island Swamp"
    oracleText = "({T}: Add {U} or {B}.)\nThis land enters tapped unless you control two or more basic lands."

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
        collectorNumber = "249"
        artist = "Adam Paquette"
        flavorText = "On the continent of Tazeem, rushing waters plunge through narrow canyons into mist-cloaked lakes."
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0dd1726f-b899-491a-8b0e-8e3d25f17d3d.jpg"
    }
}
