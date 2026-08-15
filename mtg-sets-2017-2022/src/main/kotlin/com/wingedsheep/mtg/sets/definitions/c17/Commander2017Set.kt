package com.wingedsheep.mtg.sets.definitions.c17

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Commander 2017
 *
 * Four tribal-themed preconstructed Commander decks (Cats, Dragons, Vampires, Wizards).
 * Earliest printing of Path of Ancestry — the canonical [CardDefinition] for that card
 * lives in this set's `cards/` package.
 *
 * Set Code: C17
 * Release Date: August 25, 2017
 */
object Commander2017Set : MtgSet {

    override val code = "C17"
    override val displayName = "Commander 2017"
    override val releaseDate = "2017-08-25"
    override val sealedSupported = false

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.c17.cards"
}
