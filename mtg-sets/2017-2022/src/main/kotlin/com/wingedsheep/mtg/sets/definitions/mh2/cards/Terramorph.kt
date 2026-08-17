package com.wingedsheep.mtg.sets.definitions.mh2.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Terramorph
 * {3}{G}
 * Sorcery
 * Search your library for a basic land card, put it onto the battlefield, then shuffle.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * Search for Tomorrow's spell half at a rebound price: [Patterns.Library.searchLibrary] over
 * [GameObjectFilter.BasicLand] into [SearchDestination.BATTLEFIELD], shuffling after. Unlike
 * cascade and suspend, [Keyword.REBOUND] has a real consumer — `StackResolver` reads it off
 * `cardDef.keywords` when the spell resolves — so the bare keyword is the whole implementation.
 */
val Terramorph = card("Terramorph") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Search your library for a basic land card, put it onto the battlefield, then " +
        "shuffle.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the " +
        "beginning of your next upkeep, you may cast this card from exile without paying its " +
        "mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            count = 1,
            destination = SearchDestination.BATTLEFIELD,
            shuffleAfter = true
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "177"
        artist = "Darrell Riche"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28b08596-f6c7-4366-a4ed-21a11fbc901a.jpg?1783926824"
    }
}
