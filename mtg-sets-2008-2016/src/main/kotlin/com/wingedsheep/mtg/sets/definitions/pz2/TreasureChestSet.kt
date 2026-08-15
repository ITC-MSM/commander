package com.wingedsheep.mtg.sets.definitions.pz2

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Treasure Chest (PZ2)
 *
 * MTGO-only redemption set distributed via Treasure Chest packs. Earliest printing of
 * Path of Ancestry — the canonical [CardDefinition] for that card lives in this set's
 * `cards/` package even though the card saw its first paper release in C17.
 *
 * Set Code: PZ2
 * Released: November 16, 2016
 */
object TreasureChestSet : MtgSet {

    override val code = "PZ2"
    override val displayName = "Treasure Chest"
    override val releaseDate = "2016-11-16"
    override val sealedSupported = false

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.pz2.cards"
}
