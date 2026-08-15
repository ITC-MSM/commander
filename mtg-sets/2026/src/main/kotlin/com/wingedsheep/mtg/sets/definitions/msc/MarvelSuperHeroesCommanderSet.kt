package com.wingedsheep.mtg.sets.definitions.msc

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Marvel Super Heroes Commander (2026)
 *
 * The Marvel Super Heroes Universes Beyond Commander decks.
 * Scaffolded here as the canonical home for cards whose earliest real printing is
 * MSC. Intentionally incomplete relative to the official set — only cards
 * relocated here as their canonical earliest printing live in this package.
 *
 * Set Code: MSC
 * Release Date: 2026-06-26
 */
object MarvelSuperHeroesCommanderSet : MtgSet {

    override val code = "MSC"
    override val displayName = "Marvel Super Heroes Commander"
    override val releaseDate = "2026-06-26"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.msc.cards"
}
