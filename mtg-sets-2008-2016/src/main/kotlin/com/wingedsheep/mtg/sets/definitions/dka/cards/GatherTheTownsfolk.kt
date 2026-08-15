package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Gather the Townsfolk
 * {1}{W}
 * Sorcery
 * Create two 1/1 white Human creature tokens.
 * Fateful hour — If you have 5 or less life, create five of those tokens instead.
 */
val GatherTheTownsfolk = card("Gather the Townsfolk") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create two 1/1 white Human creature tokens.\n" +
        "Fateful hour — If you have 5 or less life, create five of those tokens instead."
    spell {
        effect = ConditionalEffect(
            condition = Conditions.LifeAtMost(5),
            effect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Human"),
                count = 5
            ),
            elseEffect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Human"),
                count = 2
            )
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Dan Murayama Scott"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9cfa554b-ee6d-4d4e-aabc-fe7bc6b25236.jpg?1782714655"
    }
}
