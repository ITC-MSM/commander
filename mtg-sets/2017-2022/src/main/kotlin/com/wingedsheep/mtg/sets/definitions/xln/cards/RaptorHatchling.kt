package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Raptor Hatchling
 * {1}{R}
 * Creature — Dinosaur
 * 1/1
 *
 * Enrage — Whenever this creature is dealt damage, create a 3/3 green Dinosaur creature token
 * with trample.
 */
val RaptorHatchling = card("Raptor Hatchling") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, create a 3/3 green Dinosaur " +
        "creature token with trample."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Dinosaur"),
            keywords = setOf(Keyword.TRAMPLE),
        )
        description = "Enrage — Whenever this creature is dealt damage, create a 3/3 green " +
            "Dinosaur creature token with trample."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Even Amundsen"
        flavorText = "\"Every little hatchling has a parent's claws to guard it.\"\n—Sun Empire saying"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/8093e88d-fd3c-43d3-a025-9ebb9f02a84f.jpg"
    }
}
