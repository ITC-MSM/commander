package com.wingedsheep.mtg.sets.definitions.jou.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flurry of Horns
 * {4}{R}
 * Sorcery
 *
 * Create two 2/3 red Minotaur creature tokens with haste.
 */
val FlurryOfHorns = card("Flurry of Horns") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create two 2/3 red Minotaur creature tokens with haste."

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 3,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Minotaur"),
            keywords = setOf(Keyword.HASTE),
            count = 2,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "96"
        artist = "Phill Simmer"
        flavorText = "A minotaur does not distinguish between human, satyr, and triton. They are all meat."
        imageUri = "https://cards.scryfall.io/normal/front/3/3/333ff270-4bc7-48ee-9738-f8c70b8a7e40.jpg?1783939424"
    }
}
