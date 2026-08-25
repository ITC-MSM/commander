package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Icatian Crier
 * {2}{W}
 * Creature — Human Spellshaper
 * 1/1
 * {1}{W}, {T}, Discard a card: Create two 1/1 white Citizen creature tokens.
 */
val IcatianCrier = card("Icatian Crier") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Spellshaper"
    power = 1
    toughness = 1
    oracleText = "{1}{W}, {T}, Discard a card: Create two 1/1 white Citizen creature tokens."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap, Costs.DiscardCard)
        effect = Effects.CreateToken(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Citizen")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Michael Phillippi"
        flavorText = "A thousand years removed from her home, her news of war had lost its context, but not its relevance."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/523ab784-c77e-4b78-99fc-b5d7ed985d76.jpg"
    }
}
