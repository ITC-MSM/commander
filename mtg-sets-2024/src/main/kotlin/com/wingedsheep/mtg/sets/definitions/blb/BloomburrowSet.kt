package com.wingedsheep.mtg.sets.definitions.blb

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Bloomburrow Set (2024)
 *
 * Bloomburrow is a plane inhabited entirely by anthropomorphic animals.
 * The set features a variety of creature types including Mice, Rabbits,
 * Frogs, Otters, and more.
 *
 * Set Code: BLB
 * Release Date: August 2, 2024
 * Card Count: 261
 */
object BloomburrowSet : MtgSet {

    override val code = "BLB"
    override val displayName = "Bloomburrow"
    override val releaseDate = "2024-08-02"
    override val sealedSupported = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE, code)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.blb.cards"
}
