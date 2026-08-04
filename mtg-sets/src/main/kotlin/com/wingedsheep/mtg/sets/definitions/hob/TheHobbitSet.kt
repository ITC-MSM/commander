package com.wingedsheep.mtg.sets.definitions.hob

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * The Hobbit (2026)
 *
 * Set Code: HOB
 * Release Date: August 14, 2026
 * Preview inventory is sourced from Scryfall and may grow before release.
 */
object TheHobbitSet : MtgSet {
    override val code = "HOB"
    override val displayName = "The Hobbit"
    override val releaseDate = "2026-08-14"
    override val sealedSupported = false

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.hob.cards"
}
