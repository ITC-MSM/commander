package com.wingedsheep.mtg.sets.definitions.dmu

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Dominaria United (2022)
 *
 * Set Code: DMU
 * Release Date: September 9, 2022
 */
object DominariaUnitedSet : MtgSet {

    override val code = "DMU"
    override val displayName = "Dominaria United"
    override val releaseDate = "2022-09-09"
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.dmu.cards"
}
