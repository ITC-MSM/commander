package com.wingedsheep.mtg.sets.definitions.zen

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Zendikar (2009)
 *
 * Intentionally incomplete relative to the official set: the cards whose canonical earliest
 * printing is Zendikar, plus `Printing` rows for the reprints it carries and the set's own
 * full-art basic lands.
 *
 * Set Code: ZEN
 * Release Date: 2009-10-02
 */
object ZendikarSet : MtgSet {

    override val code = "ZEN"
    override val displayName = "Zendikar"
    override val releaseDate = "2009-10-02"
    override val block = "Zendikar"
    override val sealedSupported = false
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.zen.cards"
}
