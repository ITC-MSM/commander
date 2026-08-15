package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Samite Ministration
 * {1}{W}
 * Instant
 * Prevent all damage that would be dealt to you this turn by a source of your choice.
 * Whenever damage from a black or red source is prevented this way this turn, you gain that much life.
 */
val SamiteMinistration = card("Samite Ministration") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent all damage that would be dealt to you this turn by a source of your choice. " +
        "Whenever damage from a black or red source is prevented this way this turn, you gain that much life."

    spell {
        effect = Effects.PreventAllDamageFromChosenSource(
            gainLifeFromColors = setOf(Color.BLACK, Color.RED)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "36"
        artist = "Darrell Riche"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1de62ed-79e6-4daf-a2ab-dc0726e1f7df.jpg?1562930810"
    }
}
