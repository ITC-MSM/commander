package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Prismatic Vista
 * Land
 * {T}, Pay 1 life, Sacrifice this land: Search your library for a basic land card, put it onto
 * the battlefield, then shuffle.
 *
 * A fetch land in the Onslaught mould, but fetching any *basic* land rather than two named
 * basic land types — hence [GameObjectFilter.BasicLand] rather than a subtype disjunction.
 * The fetched land isn't revealed and doesn't enter tapped.
 */
val PrismaticVista = card("Prismatic Vista") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}, Pay 1 life, Sacrifice this land: Search your library for a basic land card, put it onto the battlefield, then shuffle."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(1), Costs.SacrificeSelf)
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = false,
            shuffleAfter = true
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "244"
        artist = "Sam Burley"
        flavorText = "There is beauty in the uncertainty of potential."
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e37da81e-be12-45a2-9128-376f1ad7b3e8.jpg?1783933066"
    }
}
