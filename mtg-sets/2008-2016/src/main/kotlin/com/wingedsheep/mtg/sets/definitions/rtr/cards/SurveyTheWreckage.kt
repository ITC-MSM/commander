package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Survey the Wreckage
 * {4}{R}
 * Sorcery
 *
 * Destroy target land. Create a 1/1 red Goblin creature token.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * Land destruction plus a consolation token. The token is created by *this* spell's controller —
 * the printed text names nobody, so it is the default.
 */
val SurveyTheWreckage = card("Survey the Wreckage") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target land. Create a 1/1 red Goblin creature token."

    spell {
        val t = target("target land", Targets.Land)
        effect = Effects.Composite(
            Effects.Destroy(t),
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.RED),
                creatureTypes = setOf("Goblin"),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "107"
        artist = "Warren Mahy"
        flavorText = "Goblins and architects seldom get along."
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6e750f9-ad86-4d60-98a3-78d11cd52cd1.jpg?1783940353"
    }
}
