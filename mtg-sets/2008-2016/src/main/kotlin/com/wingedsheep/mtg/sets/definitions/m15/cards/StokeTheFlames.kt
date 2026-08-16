package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stoke the Flames
 * {2}{R}{R}
 * Instant
 * Convoke
 * Stoke the Flames deals 4 damage to any target.
 *
 * Canonical printing: Magic 2015, the card's earliest real-expansion printing. Reprinted in MOM
 * as a `Printing` row.
 */
val StokeTheFlames = card("Stoke the Flames") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Stoke the Flames deals 4 damage to any target."

    keywords(Keyword.CONVOKE)

    spell {
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(4, victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "164"
        artist = "Ryan Barger"
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d94c000-52e0-4215-83af-6351dc43e636.jpg?1783939169"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
        ruling(
            "2024-01-12",
            "Tapping an untapped creature that's attacking or blocking to convoke a spell won't " +
                "cause that creature to stop attacking or blocking."
        )
    }
}
