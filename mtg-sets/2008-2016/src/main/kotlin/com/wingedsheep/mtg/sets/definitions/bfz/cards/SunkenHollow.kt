package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Sunken Hollow
 *
 * Land — Island Swamp
 * ({T}: Add {U} or {B}.)
 * This land enters tapped unless you control two or more basic lands.
 *
 * The battle land cycle is one line of real rules text: [EntersTapped] with an `unlessCondition`
 * of [Conditions.YouControlAtLeast] over basic lands — a count, not a mere existence check, and
 * without the "other" variant because the printed line does not say "other". The mana is *not*
 * written: the `Island Swamp` subtypes on the type line make `IntrinsicManaAbilities` grant {U}
 * and {B} already, which is exactly why the printed tap line sits in reminder parentheses.
 */
val SunkenHollow = card("Sunken Hollow") {
    manaCost = ""
    colorIdentity = "BU"
    typeLine = "Land — Island Swamp"
    oracleText = "({T}: Add {U} or {B}.)\n" +
        "This land enters tapped unless you control two or more basic lands."

    replacementEffect(
        EntersTapped(
            unlessCondition = Conditions.YouControlAtLeast(2, GameObjectFilter.BasicLand)
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "249"
        artist = "Adam Paquette"
        flavorText = "On the continent of Tazeem, rushing waters plunge through narrow canyons into mist-cloaked lakes."
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0dd1726f-b899-491a-8b0e-8e3d25f17d3d.jpg?1783938172"
    }
}
