package com.wingedsheep.mtg.sets.definitions.all

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Alliances (1996)
 *
 * Scaffolded to hold the canonical [CardDefinition]s of cards whose earliest real-expansion
 * printing is Alliances (e.g. Elvish Ranger, Storm Crow), with later sets contributing reprint
 * [Printing] rows.
 *
 * Set Code: ALL
 * Release Date: June 10, 1996
 */
object AlliancesSet : MtgSet {

    override val code = "ALL"
    override val displayName = "Alliances"
    override val releaseDate = "1996-06-10"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.all.cards"
}
