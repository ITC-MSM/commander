package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ant Queen
 * {3}{G}{G}
 * Creature — Insect
 * 5/5
 *
 * {1}{G}: Create a 1/1 green Insect creature token.
 */
val AntQueen = card("Ant Queen") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    oracleText = "{1}{G}: Create a 1/1 green Insect creature token."
    power = 5
    toughness = 5

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Insect")
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "166"
        artist = "Trevor Claxton"
        flavorText = "\"Kill the queen first, or we'll be fighting her drones forever. It is not in a queen's nature to have enough servants.\"\n—Borzard, exterminator captain"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6ee46d8b-559e-4c33-99fb-b55f00aeee72.jpg?1783942367"
    }
}
