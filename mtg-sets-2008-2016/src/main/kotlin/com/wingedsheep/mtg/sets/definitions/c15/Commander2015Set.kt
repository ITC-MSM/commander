package com.wingedsheep.mtg.sets.definitions.c15

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Commander 2015
 *
 * Set Code: C15
 * Release Date: November 13, 2015
 */
object Commander2015Set : MtgSet {

    override val code = "C15"
    override val displayName = "Commander 2015"
    override val releaseDate = "2015-11-13"
    override val sealedSupported = false

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.c15.cards"
}
