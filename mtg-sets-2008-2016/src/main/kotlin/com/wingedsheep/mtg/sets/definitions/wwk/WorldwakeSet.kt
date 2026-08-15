package com.wingedsheep.mtg.sets.definitions.wwk

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Worldwake
 *
 * The second set of the Zendikar block, released February 2010. Introduced manlands
 * (Celestial Colonnade, Raging Ravine, ...) and Jace, the Mind Sculptor.
 *
 * Set Code: WWK
 * Release Date: February 5, 2010
 */
object WorldwakeSet : MtgSet {

    override val code = "WWK"
    override val displayName = "Worldwake"
    override val releaseDate = "2010-02-05"
    override val incomplete = true
    override val sealedSupported = false

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.wwk.cards"
}
