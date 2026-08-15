package com.wingedsheep.mtg.sets.definitions.dom

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.limited.BoosterStrategy
import com.wingedsheep.sdk.limited.GuaranteedLegendaryBooster
import com.wingedsheep.sdk.limited.StandardBooster
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Dominaria Set (2018)
 *
 * Dominaria was a standalone expansion set that returned to Magic's home plane,
 * featuring historic and legendary themes.
 *
 * Set Code: DOM
 * Release Date: April 27, 2018
 * Card Count: 280
 */
object DominariaSet : MtgSet {

    override val code = "DOM"
    override val displayName = "Dominaria"
    override val releaseDate = "2018-04-27"
    override val sealedSupported = true
    // Mythic-era base (10 commons; the paper land slot is supplied at deck building).
    override val boosterStrategy: BoosterStrategy = GuaranteedLegendaryBooster(StandardBooster(commons = 10))

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.dom.cards"
}
