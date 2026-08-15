package com.wingedsheep.mtg.sets.definitions.c13

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Commander 2013 (2013)
 *
 * Scaffolded to hold cards whose canonical earliest printing is C13. Intentionally
 * incomplete relative to the official set — only cards relocated here as their canonical
 * earliest printing live in this package.
 *
 * Set Code: C13
 * Release Date: 2013-11-01
 */
object Commander2013Set : MtgSet {

    override val code = "C13"
    override val displayName = "Commander 2013"
    override val releaseDate = "2013-11-01"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.c13.cards"
}
