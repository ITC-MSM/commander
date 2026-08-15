package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cunning Maneuver
 * {1}{R}
 * Instant
 * Target creature gets +3/+1 until end of turn.
 * Create a Clue token. (It's an artifact with "{2}, Sacrifice this token: Draw a card.")
 */
val CunningManeuver = card("Cunning Maneuver") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+1 until end of turn.\n" +
        "Create a Clue token. (It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(3, 1, t),
            Effects.CreateClue()
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Robin Har"
        flavorText = "\"Is that all you got? Man, they'll make anyone an admiral these days.\"\n—Aang"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0ff7c993-ba29-43b0-9639-7c4bc0292fa2.jpg?1764120894"
    }
}
