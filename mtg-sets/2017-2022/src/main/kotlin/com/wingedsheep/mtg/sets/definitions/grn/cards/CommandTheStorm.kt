package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Command the Storm
 * {4}{R}
 * Instant
 * Command the Storm deals 5 damage to target creature.
 */
val CommandTheStorm = card("Command the Storm") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Command the Storm deals 5 damage to target creature."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.DealDamage(5, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Jason Rainville"
        flavorText = "In the wake of Niv-Mizzet's disappearance, Ral found himself leading the guild. He had dreamed of this day, but couldn't help feeling like a pawn in someone else's game."
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4144e98-957e-43ac-b107-8ccf450748df.jpg?1783934166"
    }
}
