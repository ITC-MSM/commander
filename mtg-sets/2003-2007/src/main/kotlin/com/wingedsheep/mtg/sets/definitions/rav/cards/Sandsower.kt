package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Sandsower
 * {3}{W}
 * Creature — Spirit
 * 1/3
 * Tap three untapped creatures you control: Tap target creature.
 */
val Sandsower = card("Sandsower") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText = "Tap three untapped creatures you control: Tap target creature."
    power = 1
    toughness = 3

    activatedAbility {
        cost = Costs.TapPermanents(count = 3, filter = GameObjectFilter.Creature)
        val t = target("target creature", Targets.Creature)
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "28"
        artist = "Kev Walker"
        flavorText = "It drifts through the streets as a breeze of collective sighs, wilting the bustle with dreams and heavy eyelids."
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43472fbe-d4f6-41b5-9928-965336d44a8f.jpg"
    }
}
