package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Sphinx Summoner
 * {3}{U}{B}
 * Artifact Creature — Sphinx
 * 3 / 3
 * Flying
 * When this creature enters, you may search your library for an artifact creature card, reveal it, put it into your hand, then shuffle.
 *
 * The whole printed search is one [Patterns.Library.searchLibrary]: gather the library, choose up
 * to one match, move it to hand revealed, shuffle, then emit the "a player searched their library"
 * event (CR 701.23) that the pattern already appends. The printed "you may" is `optional = true`,
 * which lowers to a `Gate.MayDecide` around the composite — the search itself asks nothing, so
 * there is exactly one prompt. [GameObjectFilter.ArtifactCreature] is the prebuilt
 * artifact-and-creature conjunction.
 */
val SphinxSummoner = card("Sphinx Summoner") {
    manaCost = "{3}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Artifact Creature — Sphinx"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "When this creature enters, you may search your library for an artifact creature card, reveal it, put it into your hand, then shuffle."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.ArtifactCreature,
            count = 1,
            destination = SearchDestination.HAND,
            shuffleAfter = true,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "127"
        artist = "Jaime Jones"
        imageUri = "https://cards.scryfall.io/normal/front/0/0/006b5000-2a8f-4020-9dbf-7170da13b54e.jpg"
    }
}
