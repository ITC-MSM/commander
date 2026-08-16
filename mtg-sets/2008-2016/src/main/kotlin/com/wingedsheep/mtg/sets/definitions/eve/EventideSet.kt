package com.wingedsheep.mtg.sets.definitions.eve

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Eventide (2008)
 *
 * Scaffolded to hold the cards whose earliest real printing is Eventide. Intentionally
 * incomplete relative to the official set.
 *
 * Set Code: EVE
 * Release Date: 2008-07-25
 */
object EventideSet : MtgSet {

    override val code = "EVE"
    override val displayName = "Eventide"
    override val releaseDate = "2008-07-25"
    override val block = "Shadowmoor"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.eve.cards"
}
