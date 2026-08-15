package com.wingedsheep.mtg.sets.definitions.bok

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Betrayers of Kamigawa (2005)
 *
 * The second set of the Kamigawa block, between Champions and Saviors.
 * Scaffolded here as the canonical home for cards whose earliest real printing is
 * BOK. Intentionally incomplete relative to the official set — only cards
 * relocated here as their canonical earliest printing live in this package.
 *
 * Set Code: BOK
 * Release Date: 2005-02-04
 */
object BetrayersOfKamigawaSet : MtgSet {

    override val code = "BOK"
    override val displayName = "Betrayers of Kamigawa"
    override val releaseDate = "2005-02-04"
    override val block = "Kamigawa"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.bok.cards"
}
