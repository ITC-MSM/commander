package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Warm Welcome
 * {2}{G}
 * Instant
 * Look at the top five cards of your library. You may reveal a creature card from among them and put it into your hand. Put the rest on the bottom of your library in a random order. Create a 1/1 green and white Citizen creature token.
 *
 * The look-at-top clause is the Star Charter shape via
 * [Patterns.Library.lookAtTopRevealMatchingToHand] — its defaults already put the remainder on the
 * bottom of the library in a random order — followed by the shared Citizen [Effects.CreateToken].
 */
val WarmWelcome = card("Warm Welcome") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Look at the top five cards of your library. You may reveal a creature card from among them and put it into your hand. Put the rest on the bottom of your library in a random order. Create a 1/1 green and white Citizen creature token."

    spell {
        effect = Effects.Composite(
            Patterns.Library.lookAtTopRevealMatchingToHand(
                count = DynamicAmount.Fixed(5),
                filter = GameObjectFilter.Creature,
                prompt = "You may reveal a creature card from among them and put it into your hand"
            ),
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN, Color.WHITE),
                creatureTypes = setOf("Citizen"),
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Inka Schulz"
        flavorText = "\"Welcome to the family. Chef Rocco sends their congratulations.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/a/bad9e58e-c9a3-4a0d-9a59-71c20a3275b6.jpg?1783923094"
    }
}
