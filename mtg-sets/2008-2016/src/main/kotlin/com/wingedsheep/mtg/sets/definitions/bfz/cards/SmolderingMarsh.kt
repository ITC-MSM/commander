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
 * Smoldering Marsh
 *
 * Land — Swamp Mountain
 * ({T}: Add {B} or {R}.)
 * This land enters tapped unless you control two or more basic lands.
 */
val SmolderingMarsh = card("Smoldering Marsh") {
    colorIdentity = "BR"
    typeLine = "Land — Swamp Mountain"
    oracleText = "({T}: Add {B} or {R}.)\nThis land enters tapped unless you control two or more basic lands."

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
        collectorNumber = "247"
        artist = "Adam Paquette"
        flavorText = "The continent of Guul Draz is a geothermal swampland reeking of heat and decay."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/359b189d-aa51-4e90-820a-e79884562e34.jpg"
    }
}
