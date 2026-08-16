package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Triplicate Spirits
 * {4}{W}{W}
 * Sorcery
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Create three 1/1 white Spirit creature tokens with flying.
 */
val TriplicateSpirits = card("Triplicate Spirits") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\nCreate three 1/1 white Spirit creature tokens with flying."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            count = 3,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d6498d3-bf1f-4bf1-a602-7c21fb44c106.jpg?1783939196"
        ruling(
            "2024-01-12",
            "You can tap any untapped creature you control to convoke a spell, even one you haven't controlled continuously since the beginning of your most recent turn.",
        )
        ruling(
            "2024-01-12",
            "Tapping an untapped creature that's attacking or blocking to convoke a spell won't cause that creature to stop attacking or blocking.",
        )
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional costs, or anything else that increases or reduces the cost to cast the spell. Convoke applies after the total cost is calculated. Convoke doesn't change a spell's mana cost or mana value.",
        )
        ruling(
            "2024-01-12",
            "If a creature you control has a mana ability with {T} in the cost, activating that ability while casting a spell with convoke will result in the creature being tapped before you pay the spell's costs. You won't be able to tap it again for convoke. Similarly, if you sacrifice a creature to activate a mana ability while casting a spell with convoke, that creature won't be on the battlefield when you pay the spell's costs, so you won't be able to tap it for convoke.",
        )
        ruling(
            "2024-01-12",
            "Because convoke isn't an alternative cost, it can be used in conjunction with alternative costs.",
        )
        ruling(
            "2024-01-12",
            "Tapping a multicolored creature using convoke will pay for {1} or one mana of your choice of any of that creature's colors.",
        )
    }
}
