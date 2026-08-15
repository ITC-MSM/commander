package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Raphael, Tough Turtle
 * {1}{R}
 * Legendary Creature — Mutant Ninja Turtle
 * 1/3
 *
 * Alliance — Whenever another creature you control enters, Raphael
 * deals 1 damage to target opponent.
 */
val RaphaelToughTurtle = card("Raphael, Tough Turtle") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Mutant Ninja Turtle"
    oracleText = "Alliance — Whenever another creature you control enters, Raphael deals 1 damage to target opponent."
    power = 1
    toughness = 3

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.DealDamage(1, opponent)
        description = "Alliance — Whenever another creature you control enters, Raphael deals 1 damage to target opponent."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Nathaniel Himawan"
        flavorText = "\"Anger is a dangerous ally. It clouds your judgment. You need to control it, lest it control you.\"\n—Splinter"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f24c90c8-ed04-4b4e-8b19-c7d07a90c6b8.jpg?1771342404"
    }
}
