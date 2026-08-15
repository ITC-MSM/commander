package com.wingedsheep.mtg.sets.definitions.fin

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Final Fantasy (2025)
 *
 * Set Code: FIN
 * Release Date: June 13, 2025
 */
object FinalFantasySet : MtgSet {

    override val code = "FIN"
    override val displayName = "Final Fantasy"
    override val releaseDate = "2025-06-13"

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.fin.cards"
}
