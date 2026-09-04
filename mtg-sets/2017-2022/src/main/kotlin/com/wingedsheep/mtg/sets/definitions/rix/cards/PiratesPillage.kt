package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pirate's Pillage
 * {3}{R}
 * Sorcery
 * As an additional cost to cast this spell, discard a card.
 * Draw two cards and create two Treasure tokens.
 */
val PiratesPillage = card("Pirate's Pillage") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, discard a card.\n" +
        "Draw two cards and create two Treasure tokens. (They're artifacts with \"{T}, " +
        "Sacrifice this token: Add one mana of any color.\")"

    additionalCost(Costs.additional.DiscardCards())

    spell {
        effect = Effects.DrawCards(2) then Effects.CreateTreasure(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "Wayne Reynolds"
        flavorText = "Goblins can carry their body weight in loot."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c99c812f-89b1-4741-a50d-8634e003a7c0.jpg?1783935297"
    }
}
