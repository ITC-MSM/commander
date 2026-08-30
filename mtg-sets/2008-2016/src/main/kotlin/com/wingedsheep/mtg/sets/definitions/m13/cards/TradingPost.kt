package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Trading Post
 * {4}
 * Artifact
 * {1}, {T}, Discard a card: You gain 4 life.
 * {1}, {T}, Pay 1 life: Create a 0 / 1 white Goat creature token.
 * {1}, {T}, Sacrifice a creature: Return target artifact card from your graveyard to your hand.
 * {1}, {T}, Sacrifice an artifact: Draw a card.
 *
 * Canonical printing: Magic 2013, the card's earliest printing. Reprinted in M14 as a `Printing`
 * row.
 *
 * Four separate activated abilities, each a [Costs.Composite] of `{1}`, the tap symbol, and one
 * additional cost. Only the third targets; the Goat token's art comes from the set's `tokenArt`
 * registry, never a baked `imageUri`.
 */
val TradingPost = card("Trading Post") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, {T}, Discard a card: You gain 4 life.\n" +
            "{1}, {T}, Pay 1 life: Create a 0/1 white Goat creature token.\n" +
            "{1}, {T}, Sacrifice a creature: Return target artifact card from your graveyard to your hand.\n" +
            "{1}, {T}, Sacrifice an artifact: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.Discard())
        effect = Effects.GainLife(4)
        description = "{1}, {T}, Discard a card: You gain 4 life."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.PayLife(1))
        effect = Effects.CreateToken(
            power = 0,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Goat")
        )
        description = "{1}, {T}, Pay 1 life: Create a 0/1 white Goat creature token."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.Sacrifice(GameObjectFilter.Creature))
        val artifact = target(
            "target artifact card from your graveyard",
            TargetObject(filter = TargetFilter.ArtifactInYourGraveyard)
        )
        effect = Effects.ReturnToHand(artifact)
        description = "{1}, {T}, Sacrifice a creature: Return target artifact card from your graveyard to your hand."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.Sacrifice(GameObjectFilter.Artifact))
        effect = Effects.DrawCards(1)
        description = "{1}, {T}, Sacrifice an artifact: Draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "220"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20604b28-d096-40f8-a30c-3bc89e708676.jpg"
    }
}
