package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Search for Tomorrow
 * {2}{G}
 * Sorcery
 * Search your library for a basic land card, put it onto the battlefield, then shuffle.
 * Suspend 2—{G} (Rather than cast this card from your hand, you may pay {G} and exile it with two time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)
 *
 * The spell half is the stock ramp tutor, [Patterns.Library.searchLibrary] over
 * [GameObjectFilter.BasicLand] into [SearchDestination.BATTLEFIELD] with a shuffle after.
 * `Keyword.SUSPEND` is display-only — the engine's `SuspendEnumerator` reads the parameterized
 * [KeywordAbility.Suspend] instead, so suspend is written as `suspend("{G}", 2)` (cost first,
 * time counters second) exactly as on `tsp/cards/AncestralVision.kt`; the keyword is derived from it.
 */
val SearchForTomorrow = card("Search for Tomorrow") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Search your library for a basic land card, put it onto the battlefield, then " +
        "shuffle.\n" +
        "Suspend 2—{G} (Rather than cast this card from your hand, you may pay {G} and exile it " +
        "with two time counters on it. At the beginning of your upkeep, remove a time counter. " +
        "When the last is removed, you may cast it without paying its mana cost.)"

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            count = 1,
            destination = SearchDestination.BATTLEFIELD,
            shuffleAfter = true
        )
    }

    keywordAbility(KeywordAbility.suspend("{G}", 2))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "216"
        artist = "Randy Gallegos"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56f739e8-b4ba-426a-a159-0e0d5a0ebb6f.jpg?1783943208"
    }
}
