package com.wingedsheep.mtg.sets.definitions.arb

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Alara Reborn (2009)
 *
 * mtgish-tooling seed: only the cards relocated here as their canonical earliest printing.
 * Intentionally incomplete relative to the official set.
 *
 * Set Code: ARB
 * Release Date: 2009-04-30
 */
object AlaraRebornSet : MtgSet {

    override val code = "ARB"
    override val displayName = "Alara Reborn"
    override val releaseDate = "2009-04-30"
    override val block = "Shards of Alara"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.arb.cards"
}
