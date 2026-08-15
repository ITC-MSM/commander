package com.wingedsheep.mtg.sets.definitions.dis

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Dissension (2006)
 *
 * mtgish-tooling auto-generated seed: only the cards relocated here as their canonical
 * earliest printing. Intentionally incomplete relative to the official set.
 *
 * Set Code: DIS
 * Release Date: 2006-05-05
 */
object DissensionSet : MtgSet {

    override val code = "DIS"
    override val displayName = "Dissension"
    override val releaseDate = "2006-05-05"
    override val block = "Ravnica"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.dis.cards"
}
