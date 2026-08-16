package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Halo Hopper
 * {3}
 * Artifact Creature — Frog
 * 3/2
 * Convoke
 */
val HaloHopper = card("Halo Hopper") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Frog"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)"
    power = 3
    toughness = 2

    keywords(Keyword.CONVOKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "260"
        artist = "Daniel Ljunggren"
        flavorText = "Folded by an imaginative child, animated by a mischievous kami, and blessed " +
            "by an otherworldly angel."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e07baeb1-c873-42c2-8f1e-757f13572079.jpg?1783916937"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}
