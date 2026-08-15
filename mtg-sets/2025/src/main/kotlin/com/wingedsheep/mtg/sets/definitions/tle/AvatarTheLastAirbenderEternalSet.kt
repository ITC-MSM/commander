package com.wingedsheep.mtg.sets.definitions.tle

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Avatar: The Last Airbender Eternal (2025)
 *
 * The Eternal companion product to Avatar: The Last Airbender.
 * Scaffolded here as the canonical home for cards whose earliest real printing is
 * TLE. Intentionally incomplete relative to the official set — only cards
 * relocated here as their canonical earliest printing live in this package.
 *
 * Set Code: TLE
 * Release Date: 2025-11-21
 */
object AvatarTheLastAirbenderEternalSet : MtgSet {

    override val code = "TLE"
    override val displayName = "Avatar: The Last Airbender Eternal"
    override val releaseDate = "2025-11-21"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.tle.cards"
}
