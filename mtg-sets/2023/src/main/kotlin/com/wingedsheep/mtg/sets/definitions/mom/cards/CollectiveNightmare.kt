package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Collective Nightmare
 * {2}{B}
 * Instant
 * Convoke
 * Target creature gets -3/-3 until end of turn.
 */
val CollectiveNightmare = card("Collective Nightmare") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Target creature gets -3/-3 until end of turn."

    keywords(Keyword.CONVOKE)

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-3, -3, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "95"
        artist = "Rovina Cai"
        flavorText = "Night fell all at once. Specifically, on the Phyrexian."
        imageUri = "https://cards.scryfall.io/normal/front/0/5/05864d3a-d8bb-4ddf-b721-883632050cb1.jpg?1783917017"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}
