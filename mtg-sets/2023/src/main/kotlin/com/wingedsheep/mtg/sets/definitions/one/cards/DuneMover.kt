package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Dune Mover
 * {2}
 * Artifact Creature — Phyrexian Golem
 * 2/1
 *
 * Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)
 * When this creature enters, you may search your library for a basic land card, reveal it, then shuffle and put that card on top.
 */
val DuneMover = card("Dune Mover") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Phyrexian Golem"
    power = 2
    toughness = 1
    oracleText = "Toxic 1 (Players dealt combat damage by this creature also get a poison counter.)\n" +
        "When this creature enters, you may search your library for a basic land card, reveal it, then shuffle and put that card on top."

    keywordAbility(KeywordAbility.Numeric(Keyword.TOXIC, 1))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.TOP_OF_LIBRARY,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "226"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29d2c689-af37-433a-a012-e8d384702811.jpg?1783917993"
    }
}
