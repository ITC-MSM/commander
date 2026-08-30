package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Shrivel
 * {1}{B}
 * Sorcery
 * All creatures get -1/-1 until end of turn.
 *
 * Canonical printing: Rise of the Eldrazi, the card's earliest printing. Reprinted in M14 as a
 * `Printing` row.
 */
val Shrivel = card("Shrivel") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "All creatures get -1/-1 until end of turn."

    spell {
        effect = Patterns.Group.modifyStatsForAll(-1, -1, GroupFilter.AllCreatures)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "Jung Park"
        flavorText = "\"Have you ever killed insects nibbling at your crops? I think that's what the Eldrazi believe they're doing to us.\"\n" +
            "—Sheyda, Ondu gamekeeper"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a87c80a1-5818-45fd-9a37-a2ee3396626e.jpg"
    }
}
