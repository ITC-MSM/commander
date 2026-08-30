package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Planar Cleansing
 * {3}{W}{W}{W}
 * Sorcery
 * Destroy all nonland permanents.
 *
 * Canonical printing: Magic 2010, the card's earliest printing. Reprinted in M14 as a `Printing`
 * row.
 *
 * "All nonland permanents" is the gather-then-move pipeline
 * ([Patterns.Group.destroyAllPipeline]), not a per-permanent iteration: the group is named once
 * and every member leaves together.
 */
val PlanarCleansing = card("Planar Cleansing") {
    manaCost = "{3}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy all nonland permanents."

    spell {
        effect = Patterns.Group.destroyAllPipeline(GameObjectFilter.NonlandPermanent)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "24"
        artist = "Michael Komarck"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30ee0d57-e404-4599-9b6e-f8ab8a95f9fa.jpg"
    }
}
