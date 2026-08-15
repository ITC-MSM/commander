package com.wingedsheep.mtg.sets.definitions.khc

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Kaldheim Commander
 *
 * Set Code: KHC
 */
object KaldheimCommanderSet : MtgSet {

    override val code = "KHC"
    override val displayName = "Kaldheim Commander"
    override val releaseDate = "2021-02-05"
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.khc.cards"
}
