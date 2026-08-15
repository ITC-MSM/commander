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
 * Cinder Glade
 *
 * Land — Mountain Forest
 * ({T}: Add {R} or {G}.)
 * This land enters tapped unless you control two or more basic lands.
 */
val CinderGlade = card("Cinder Glade") {
    colorIdentity = "RG"
    typeLine = "Land — Mountain Forest"
    oracleText = "({T}: Add {R} or {G}.)\nThis land enters tapped unless you control two or more basic lands."

    // Mana abilities are intrinsic from basic land types (Mountain → {R}, Forest → {G})

    replacementEffect(EntersTapped(
        unlessCondition = Compare(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.BasicLand),
            ComparisonOperator.GTE,
            DynamicAmount.Fixed(2)
        )
    ))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "235"
        artist = "Adam Paquette"
        flavorText = "On the volcanic continent of Akoum, bizarre vegetation clusters around gas vents, and jagged mountain peaks rise high into the air."
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfabc340-0391-4019-8e96-f010177b1d68.jpg?1562944436"
    }
}
