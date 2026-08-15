package com.wingedsheep.mtg.sets.definitions.ice

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Ice Age (1995)
 *
 * Scaffolded to hold the canonical [CardDefinition]s of cards whose earliest real-expansion
 * printing is Ice Age (e.g. Pyroclasm, Nature's Lore, Mountain Goat), with later sets contributing
 * reprint [Printing] rows.
 *
 * Set Code: ICE
 * Release Date: June 3, 1995
 */
object IceAgeSet : MtgSet {

    override val code = "ICE"
    override val displayName = "Ice Age"
    override val releaseDate = "1995-06-03"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.ice.cards"
}
