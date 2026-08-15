package com.wingedsheep.mtg.sets.definitions.ala

import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Shards of Alara Set (2008)
 *
 * Shards of Alara is the first set in the Alara block. The plane of Alara is
 * split into five shards, each tied to three allied colors of mana. It
 * introduced devour, exalted, unearth, and the cycling of three-color shards.
 *
 * Set Code: ALA
 * Release Date: October 3, 2008
 * Card Count: 250
 */
object ShardsOfAlaraSet : MtgSet {

    override val code = "ALA"
    override val displayName = "Shards of Alara"
    override val releaseDate = "2008-10-03"
    override val block = "Alara"
    override val basicLandsFallback = PortalSet
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.ala.cards"
}
