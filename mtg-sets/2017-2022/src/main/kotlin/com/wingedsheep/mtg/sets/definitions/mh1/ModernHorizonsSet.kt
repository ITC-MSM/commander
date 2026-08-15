package com.wingedsheep.mtg.sets.definitions.mh1

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Modern Horizons
 *
 * Set Code: MH1
 */
object ModernHorizonsSet : MtgSet {

    override val code = "MH1"
    override val displayName = "Modern Horizons"
    override val releaseDate = "2019-06-14"
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.mh1.cards"
}
