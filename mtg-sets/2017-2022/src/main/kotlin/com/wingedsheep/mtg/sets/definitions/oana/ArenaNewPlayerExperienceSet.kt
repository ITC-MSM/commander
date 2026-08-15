package com.wingedsheep.mtg.sets.definitions.oana

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Arena New Player Experience Cards (2018)
 *
 * MTG Arena's original new-player tutorial set; the earliest printing of a handful of simple creatures.
 * Scaffolded here as the canonical home for cards whose earliest real printing is
 * OANA. Intentionally incomplete relative to the official set — only cards
 * relocated here as their canonical earliest printing live in this package.
 *
 * Set Code: OANA
 * Release Date: 2018-07-14
 */
object ArenaNewPlayerExperienceSet : MtgSet {

    override val code = "OANA"
    override val displayName = "Arena New Player Experience Cards"
    override val releaseDate = "2018-07-14"
    override val sealedSupported = false
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.oana.cards"
}
