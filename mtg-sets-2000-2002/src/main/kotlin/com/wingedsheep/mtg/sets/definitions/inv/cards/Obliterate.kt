package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Obliterate
 * {6}{R}{R}
 * Sorcery
 * This spell can't be countered.
 * Destroy all artifacts, creatures, and lands. They can't be regenerated.
 *
 * "All artifacts, creatures, and lands" is a single destruction event over the
 * combined filter, so everything goes to the graveyard simultaneously (and leaves /
 * dies triggers all see the same game state).
 */
val Obliterate = card("Obliterate") {
    manaCost = "{6}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "This spell can't be countered.\n" +
        "Destroy all artifacts, creatures, and lands. They can't be regenerated."

    cantBeCountered = true

    spell {
        effect = Effects.DestroyAll(
            filter = GameObjectFilter.Artifact or GameObjectFilter.Creature or GameObjectFilter.Land,
            noRegenerate = true,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "156"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdabde40-2143-4677-b7b4-ea8fbf9b1f25.jpg?1562936357"
    }
}
