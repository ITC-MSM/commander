package com.wingedsheep.mtg.sets.definitions.fra

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Reality Fracture (2026)
 *
 * Scaffolded to hold the canonical [CardDefinition]s of cards whose earliest real printing is
 * Reality Fracture, with later sets contributing reprint [Printing] rows. Intentionally
 * incomplete relative to the official set.
 *
 * Set Code: FRA
 * Release Date: 2026-10-02
 */
object RealityFractureSet : MtgSet {

    override val code = "FRA"
    override val displayName = "Reality Fracture"
    override val releaseDate = "2026-10-02"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.fra.cards"
}
