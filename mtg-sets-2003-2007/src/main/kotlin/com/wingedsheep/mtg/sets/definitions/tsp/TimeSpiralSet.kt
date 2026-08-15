package com.wingedsheep.mtg.sets.definitions.tsp

import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Time Spiral (2006)
 *
 * Set Code: TSP
 * Release Date: October 6, 2006
 */
object TimeSpiralSet : MtgSet {

    override val code = "TSP"
    override val displayName = "Time Spiral"
    override val releaseDate = "2006-10-06"
    override val block = "Time Spiral"
    override val basicLandsFallback = PortalSet
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.tsp.cards"
}
