package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Treefolk Harbinger
 * {G}
 * Creature — Treefolk Druid
 * 0/3
 * When this creature enters, you may search your library for a Treefolk or Forest card, reveal it,
 * then shuffle and put that card on top.
 */
val TreefolkHarbinger = card("Treefolk Harbinger") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Druid"
    power = 0
    toughness = 3
    oracleText = "When this creature enters, you may search your library for a Treefolk or Forest card, reveal it, " +
        "then shuffle and put that card on top."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withAnySubtype("Treefolk", "Forest"),
            count = 1,
            destination = SearchDestination.TOP_OF_LIBRARY,
            shuffleAfter = true,
            reveal = true
        )
        description = "you may search your library for a Treefolk or Forest card, reveal it, then shuffle and " +
            "put that card on top."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "239"
        artist = "Larry MacDougall"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9ef45cdc-2e1c-40c7-8978-b09a50f511fb.jpg?1783942856"
    }
}
