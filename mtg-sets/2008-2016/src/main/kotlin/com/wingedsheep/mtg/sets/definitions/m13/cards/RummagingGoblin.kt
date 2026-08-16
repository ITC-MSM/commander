package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rummaging Goblin
 * {2}{R}
 * Creature — Goblin Rogue
 * 1/1
 * {T}, Discard a card: Draw a card.
 *
 * The discard is part of the activation cost ([Costs.DiscardCard]), not an effect — with an empty
 * hand the ability can't be activated at all.
 */
val RummagingGoblin = card("Rummaging Goblin") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 1
    toughness = 1
    oracleText = "{T}, Discard a card: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.DiscardCard)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Karl Kopinski"
        flavorText = "To a goblin, value is based on the four S's: shiny, stabby, smelly, and super smelly."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc5b622c-83a4-477e-a99c-2674e2bd6bb9.jpg?1783940480"

        ruling(
            "2012-07-01",
            "Discarding a card is part of the cost to activate Rummaging Goblin's ability. If you " +
                "don't have a card in your hand, you can't pay this part of the cost and you can't " +
                "activate the ability."
        )
    }
}
