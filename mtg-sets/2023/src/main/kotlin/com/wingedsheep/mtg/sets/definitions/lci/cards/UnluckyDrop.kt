package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Unlucky Drop
 * {3}{U}
 * Instant
 *
 * Target artifact or creature's owner puts it on their choice of the top or bottom of their library.
 */
val UnluckyDrop = card("Unlucky Drop") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText =
        "Target artifact or creature's owner puts it on their choice of the top or bottom of their library."

    spell {
        val permanent = target(
            "target artifact or creature to put on top or bottom of library",
            Targets.CreatureOrArtifact,
        )
        effect = Effects.PutOnTopOrBottomOfLibrary(permanent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Patrik Hell"
        flavorText = "\"First rule down here is the same as it is on the water: always double-check " +
            "your knots.\"\n—Captain Lannery Storm"
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76ebb2df-8891-434e-9cd0-f25b848cb754.jpg?1782694544"
    }
}
