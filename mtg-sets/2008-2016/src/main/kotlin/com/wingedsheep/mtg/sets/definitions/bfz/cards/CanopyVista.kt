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
 * Canopy Vista
 *
 * Land — Forest Plains
 * ({T}: Add {G} or {W}.)
 * This land enters tapped unless you control two or more basic lands.
 */
val CanopyVista = card("Canopy Vista") {
    colorIdentity = "GW"
    typeLine = "Land — Forest Plains"
    oracleText = "({T}: Add {G} or {W}.)\nThis land enters tapped unless you control two or more basic lands."

    // Mana abilities are intrinsic from basic land types (Forest → {G}, Plains → {W})

    replacementEffect(EntersTapped(
        unlessCondition = Compare(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.BasicLand),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(2)
        )
    ))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "234"
        artist = "Adam Paquette"
        flavorText = "The continent of Murasa lies beneath a blanket of dense vegetation, its enormous branches tangled so thickly that some inhabitants never see the ground."
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3262c7f-4fdf-4648-ba59-9279c75d222d.jpg?1783938174"
    }
}
