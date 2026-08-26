package com.wingedsheep.mtg.sets.definitions.hop

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Planechase (2009)
 *
 * Scaffolded so cards whose earliest real printing is Planechase can hold their canonical
 * definition here. Intentionally incomplete relative to the official product.
 *
 * Set Code: HOP
 * Release Date: 2009-09-04
 */
object PlanechaseSet : MtgSet {

    override val code = "HOP"
    override val displayName = "Planechase"
    override val releaseDate = "2009-09-04"
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.hop.cards"
}
