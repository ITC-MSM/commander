package com.wingedsheep.mtg.sets.definitions.hml

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Homelands (1995)
 *
 * Scaffolded to hold the canonical [CardDefinition]s of cards whose earliest real-expansion
 * printing is Homelands (e.g. Dry Spell), with later sets contributing reprint [Printing] rows.
 *
 * Set Code: HML
 * Release Date: October 1, 1995
 */
object HomelandsSet : MtgSet {

    override val code = "HML"
    override val displayName = "Homelands"
    override val releaseDate = "1995-10-01"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.hml.cards"
}
