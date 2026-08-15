package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.predicates.CardPredicate

/**
 * Dragonstorm Forecaster — Tarkir: Dragonstorm #43
 * {U}
 * Creature — Human Scout
 * 0/3
 *
 * {2}, {T}: Search your library for a card named Dragonstorm Globe or Boulderborn
 * Dragon, reveal it, put it into your hand, then shuffle.
 *
 * The search is restricted to the two named cards via an OR of two
 * [CardPredicate.NameEquals] predicates. It is a mandatory tutor (no "may"), but a
 * library search may always fail to find — if neither named card is in the library
 * the player simply finds nothing, then shuffles.
 */
val DragonstormForecaster = card("Dragonstorm Forecaster") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Scout"
    power = 0
    toughness = 3
    oracleText = "{2}, {T}: Search your library for a card named Dragonstorm Globe or Boulderborn Dragon, " +
        "reveal it, put it into your hand, then shuffle."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.copy(
                cardPredicates = listOf(
                    CardPredicate.Or(
                        listOf(
                            CardPredicate.NameEquals("Dragonstorm Globe"),
                            CardPredicate.NameEquals("Boulderborn Dragon")
                        )
                    )
                )
            ),
            count = 1,
            destination = SearchDestination.HAND,
            shuffleAfter = true,
            reveal = true
        )
        description = "{2}, {T}: Search your library for a card named Dragonstorm Globe or Boulderborn Dragon, " +
            "reveal it, put it into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Kev Fang"
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75ec7a31-1893-493c-926b-dc3a8a770e72.jpg?1743204133"
    }
}
