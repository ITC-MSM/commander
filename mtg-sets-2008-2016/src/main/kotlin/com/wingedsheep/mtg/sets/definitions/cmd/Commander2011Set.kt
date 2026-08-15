package com.wingedsheep.mtg.sets.definitions.cmd

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Commander 2011
 *
 * The original Commander preconstructed deck product (five mono-color-pair decks).
 * Notable for introducing Command Tower and many other commander-format staples.
 *
 * Set Code: CMD
 * Release Date: June 17, 2011
 */
object Commander2011Set : MtgSet {

    override val code = "CMD"
    override val displayName = "Commander 2011"
    override val releaseDate = "2011-06-17"
    override val sealedSupported = false

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.cmd.cards"
}
