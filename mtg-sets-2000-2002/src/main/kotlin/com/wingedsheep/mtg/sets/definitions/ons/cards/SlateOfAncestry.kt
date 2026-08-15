package com.wingedsheep.mtg.sets.definitions.ons.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Slate of Ancestry
 * {4}
 * Artifact
 * {4}, {T}, Discard your hand: Draw a card for each creature you control.
 */
val SlateOfAncestry = card("Slate of Ancestry") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{4}, {T}, Discard your hand: Draw a card for each creature you control."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{4}"),
            Costs.Tap,
            Costs.DiscardHand
        )
        effect = Effects.DrawCards(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "310"
        artist = "Corey D. Macourek"
        flavorText = "\"The pattern of life can be studied like a book, if you know how to read it.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae596e8c-04f5-48b0-b5e2-683c74912e85.jpg?1562936203"
    }
}
