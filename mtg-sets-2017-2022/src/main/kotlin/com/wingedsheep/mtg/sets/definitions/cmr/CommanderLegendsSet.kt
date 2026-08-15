package com.wingedsheep.mtg.sets.definitions.cmr

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Commander Legends (2020)
 *
 * The first Magic set designed to be drafted for Commander — a large set mixing brand-new
 * legendary creatures with a deep pool of multiplayer-staple reprints.
 *
 * Set Code: CMR
 * Release Date: November 20, 2020
 * Card Count: 361 (official)
 *
 * This package is an mtgish-tooling auto-generated seed: the cards first printed in CMR plus
 * reprint [Printing] rows for cards whose canonical definition already lives in an earlier set.
 * It is intentionally incomplete relative to the official set.
 */
object CommanderLegendsSet : MtgSet {

    override val code = "CMR"
    override val displayName = "Commander Legends"
    override val releaseDate = "2020-11-20"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.cmr.cards"
}
