package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Meditate
 * {2}{U}
 * Instant
 * Draw four cards. You skip your next turn.
 */
val Meditate = card("Meditate") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Draw four cards. You skip your next turn."

    spell {
        effect = Effects.Composite(
            Effects.DrawCards(4),
            Effects.SkipNextTurn()
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "76"
        artist = "Susan Van Camp"
        flavorText = "\"Part of me believes that Barrin taught me meditation simply to shut me up.\"\n" +
            "—Ertai, wizard adept"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edb79a97-c1fc-4aa3-bb13-3d24a6dabeea.jpg"
    }
}
