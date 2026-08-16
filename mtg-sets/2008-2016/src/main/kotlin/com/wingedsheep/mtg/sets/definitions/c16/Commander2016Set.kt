package com.wingedsheep.mtg.sets.definitions.c16

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Commander 2016
 *
 * Scaffolded to hold the cards whose earliest real printing is Commander 2016. Intentionally
 * incomplete relative to the official set.
 *
 * Set Code: C16
 * Release Date: 2016-11-11
 */
object Commander2016Set : MtgSet {

    override val code = "C16"
    override val displayName = "Commander 2016"
    override val releaseDate = "2016-11-11"
    override val sealedSupported = false
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.c16.cards"
}
