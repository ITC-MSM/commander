package com.wingedsheep.mtg.sets.definitions.voc

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Innistrad: Crimson Vow Commander (2021)
 *
 * Commander preconstructed decks released alongside Innistrad: Crimson Vow. Scaffolded to hold the
 * canonical [CardDefinition] for cards that first appeared here and are reprinted in later sets
 * (e.g. Crossway Troublemakers, reprinted in Foundations).
 *
 * Set Code: VOC
 * Release Date: November 19, 2021
 */
object InnistradCrimsonVowCommanderSet : MtgSet {

    override val code = "VOC"
    override val displayName = "Innistrad: Crimson Vow Commander"
    override val releaseDate = "2021-11-19"
    override val sealedSupported = false
    override val incomplete = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.voc.cards"
}
