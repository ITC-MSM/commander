package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Blood Divination
 * {3}{B}
 * Sorcery
 *
 * As an additional cost to cast this spell, sacrifice a creature.
 * Draw three cards.
 */
val BloodDivination = card("Blood Divination") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\nDraw three cards."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    spell {
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "86"
        artist = "Filip Burburan"
        flavorText = "Predicting the future is a messy business."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6fe72bd9-825e-4451-9314-826882f75c85.jpg?1783934576"
    }
}
