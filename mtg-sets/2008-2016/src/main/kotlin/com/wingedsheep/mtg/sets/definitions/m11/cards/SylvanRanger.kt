package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Sylvan Ranger
 * {1}{G}
 * Creature — Elf Scout Ranger
 * 1/1
 *
 * When this creature enters, you may search your library for a basic land card, reveal it, put it into your hand, then shuffle.
 */
val SylvanRanger = card("Sylvan Ranger") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Scout Ranger"
    oracleText = "When this creature enters, you may search your library for a basic land card, reveal it, put it into your hand, then shuffle."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = Filters.BasicLand,
            destination = SearchDestination.HAND,
            reveal = true,
        )
        description = "When this creature enters, you may search your library for a basic land card, " +
            "reveal it, put it into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "198"
        artist = "Christopher Moeller"
        flavorText = "\"Not all paths are found on the forest floor.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f73ea30-c6d2-4224-ae54-f6b89e006cf4.jpg?1783941792"
    }
}
