package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.dsl.Costs
/**
 * Big Score
 * {3}{R}
 * Instant
 *
 * As an additional cost to cast this spell, discard a card.
 * Draw two cards and create two Treasure tokens.
 */
val BigScore = card("Big Score") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, discard a card.\nDraw two cards and create two Treasure tokens. (They're artifacts with \"{T}, Sacrifice this token: Add one mana of any color.\")"

    additionalCost(Costs.additional.DiscardCards())

    spell {
        effect = Effects.Composite(
            listOf(
                Effects.DrawCards(2),
                Effects.CreateTreasure(count = 2)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Gaboleps"
        flavorText = "\"Unimaginable riches? I assure you, I have a *vivid* imagination.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39d1578f-e2cf-4b93-8204-ed5434feb183.jpg?1783923123"
    }
}
