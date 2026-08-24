package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Overwhelm
 * {5}{G}{G}
 * Sorcery
 * Convoke
 * Creatures you control get +3/+3 until end of turn.
 *
 * Canonical printing: Ravnica: City of Guilds, the card's earliest real-expansion printing.
 * Reprinted in M15 as a `Printing` row.
 */
val Overwhelm = card("Overwhelm") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText =
        "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Creatures you control get +3/+3 until end of turn."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Patterns.Group.modifyStatsForAll(3, 3, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "175"
        artist = "Wayne Reynolds"
        flavorText = "\"Let the song of Selesnya be heard above the rhythm of our thundering hordes!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/7/672f6cc6-52ed-417f-b816-5733a71566e8.jpg?1783943633"
    }
}
