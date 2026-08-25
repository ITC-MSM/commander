package com.wingedsheep.mtg.sets.definitions.drb

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * From the Vault: Dragons
 *
 * A fifteen-card premium reprint product, all dragons, all foil. It carries no boosters and no
 * basic lands, so it is not a sealed/draft environment — but it is the earliest real printing of
 * Hellkite Overlord, which Shards of Alara later reprinted.
 *
 * Set Code: DRB
 * Release Date: August 29, 2008
 */
object FromTheVaultDragonsSet : MtgSet {

    override val code = "DRB"
    override val displayName = "From the Vault: Dragons"
    override val releaseDate = "2008-08-29"
    override val incomplete = true
    override val sealedSupported = false

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.drb.cards"
}
