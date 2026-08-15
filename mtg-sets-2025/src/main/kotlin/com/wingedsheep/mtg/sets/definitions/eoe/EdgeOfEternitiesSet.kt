package com.wingedsheep.mtg.sets.definitions.eoe

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Edge of Eternities Set (2025)
 *
 * Set Code: EOE
 * Release Date: August 1, 2025
 * Card Count: 261
 */
object EdgeOfEternitiesSet : MtgSet {

    override val code = "EOE"
    override val displayName = "Edge of Eternities"
    override val releaseDate = "2025-08-01"
    override val sealedSupported = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.eoe.cards"
}
