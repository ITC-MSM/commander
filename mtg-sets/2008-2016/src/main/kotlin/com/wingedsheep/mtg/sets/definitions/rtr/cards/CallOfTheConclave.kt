package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Call of the Conclave
 * {G}{W}
 * Sorcery
 *
 * Create a 3/3 green Centaur creature token.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * The plain [Effects.CreateToken] facade and nothing else.
 */
val CallOfTheConclave = card("Call of the Conclave") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Sorcery"
    oracleText = "Create a 3/3 green Centaur creature token."

    spell {
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Centaur"),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "146"
        artist = "Terese Nielsen"
        flavorText = "Centaurs are sent to evangelize in Gruul territories where words of war speak louder than prayers of peace."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6df8f4d-a07a-4664-878d-efec8b2affb9.jpg?1783940344"
    }
}
