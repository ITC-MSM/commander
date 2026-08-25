package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Amrou Scout
 * {1}{W}
 * Creature — Kithkin Rebel Scout
 * 2/1
 * {4}, {T}: Search your library for a Rebel permanent card with mana value 3 or less, put it
 * onto the battlefield, then shuffle.
 *
 * The Rebel searcher's fetch is the plain library-search pipeline
 * ([Patterns.Library.searchLibrary]) pointed at the battlefield: the filter is a *permanent* card
 * (not just a creature — Rebel is also worn by noncreature permanents), carrying the mana-value
 * ceiling, and the search shuffles afterwards.
 */
val AmrouScout = card("Amrou Scout") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Rebel Scout"
    power = 2
    toughness = 1
    oracleText = "{4}, {T}: Search your library for a Rebel permanent card with mana value 3 or less, put it onto the battlefield, then shuffle."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.REBEL).manaValueAtMost(3),
            destination = SearchDestination.BATTLEFIELD,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Quinton Hoover"
        flavorText = "The people of Amrou were scattered by war and driven into hiding. Scouts maintain the beacon fires that signal \"return\" and \"home.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea3e05e5-1340-4010-b39b-3571a5829840.jpg"
    }
}
