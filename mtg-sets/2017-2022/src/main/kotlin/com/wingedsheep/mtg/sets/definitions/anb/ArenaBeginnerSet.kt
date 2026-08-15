package com.wingedsheep.mtg.sets.definitions.anb

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Arena Beginner Set (2020)
 *
 * MTG Arena's beginner set, which succeeded the New Player Experience cards.
 * Scaffolded here as the canonical home for cards whose earliest real printing is
 * ANB. Intentionally incomplete relative to the official set — only cards
 * relocated here as their canonical earliest printing live in this package.
 *
 * Set Code: ANB
 * Release Date: 2020-08-13
 */
object ArenaBeginnerSet : MtgSet {

    override val code = "ANB"
    override val displayName = "Arena Beginner Set"
    override val releaseDate = "2020-08-13"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.anb.cards"
}
