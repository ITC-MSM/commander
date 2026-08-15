package com.wingedsheep.mtg.sets.definitions.gs1

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Global Series: Jiang Yanggu & Mu Yanling (2018)
 *
 * Scaffolded to host the earliest real printing of cards later reprinted in newer sets
 * (e.g. Ancestor Dragon, reprinted in Foundations). Intentionally incomplete relative to the
 * official product — only cards with a canonical [CardDefinition] here are present.
 *
 * Set Code: GS1
 * Release Date: 2018-06-22
 */
object GlobalSeriesSet : MtgSet {

    override val code = "GS1"
    override val displayName = "Global Series: Jiang Yanggu & Mu Yanling"
    override val releaseDate = "2018-06-22"
    override val sealedSupported = false
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.gs1.cards"
}
