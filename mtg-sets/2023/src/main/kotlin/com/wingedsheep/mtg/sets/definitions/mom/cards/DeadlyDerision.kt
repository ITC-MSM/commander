package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Deadly Derision
 * {2}{B}{B}
 * Instant
 * Destroy target creature or planeswalker. Create a Treasure token.
 *
 * Both clauses are one spell over one target: if the target is illegal on resolution the spell is
 * countered by game rules (CR 608.2b) and no Treasure is made either — the printed ruling.
 */
val DeadlyDerision = card("Deadly Derision") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target creature or planeswalker. Create a Treasure token. (It's an " +
        "artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"

    spell {
        val victim = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        effect = Effects.Destroy(victim) then Effects.CreateTreasure(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Gaboleps"
        flavorText = "Daretti looked down disdainfully. \"You call yourself machines? Where's the " +
            "elegance? Nothing but ugly piles of scrap.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1ec1a02f-128c-44aa-b708-6fcda34b40c0.jpg?1783917014"
        ruling(
            "2023-04-14",
            "If the target of Deadly Derision is illegal as the spell tries to resolve, it won't " +
                "resolve and none of its effects will happen. You won't create a Treasure token."
        )
    }
}
