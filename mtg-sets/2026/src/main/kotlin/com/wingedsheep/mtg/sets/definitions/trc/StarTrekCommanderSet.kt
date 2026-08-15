package com.wingedsheep.mtg.sets.definitions.trc

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Star Trek Commander (2026)
 *
 * The Star Trek Universes Beyond Commander decks.
 * Scaffolded here as the canonical home for cards whose earliest real printing is
 * TRC. Intentionally incomplete relative to the official set — only cards
 * relocated here as their canonical earliest printing live in this package.
 *
 * Set Code: TRC
 * Release Date: 2026-01-23
 */
object StarTrekCommanderSet : MtgSet {

    override val code = "TRC"
    override val displayName = "Star Trek Commander"
    override val releaseDate = "2026-01-23"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.trc.cards"
}
