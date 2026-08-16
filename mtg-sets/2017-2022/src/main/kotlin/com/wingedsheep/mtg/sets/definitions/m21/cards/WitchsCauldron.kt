package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Witch's Cauldron
 * {B}
 * Artifact
 * {1}{B}, {T}, Sacrifice a creature: You gain 1 life and draw a card.
 */
val WitchsCauldron = card("Witch's Cauldron") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "{1}{B}, {T}, Sacrifice a creature: You gain 1 life and draw a card."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{B}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Creature)
        )
        effect = Effects.GainLife(1)
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "129"
        artist = "Jason A. Engle"
        flavorText = "The best recipes start with reliable cookware."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d43a3eb7-3daf-4667-b824-1f5d801c9341.jpg?1783930697"
    }
}
