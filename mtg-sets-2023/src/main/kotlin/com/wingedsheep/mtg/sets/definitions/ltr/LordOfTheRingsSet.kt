package com.wingedsheep.mtg.sets.definitions.ltr

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet

/**
 * The Lord of the Rings: Tales of Middle-earth (2023)
 *
 * A 291-card Universes Beyond set (261 draftable + 30 starter/special/basic-land cards).
 * Its headline mechanics — **The Ring tempts you** / Ring-bearer and **Amass Orcs** —
 * introduce new game state and keywords that require engine work and are tracked
 * separately in `backlog/sets/lord-of-the-rings/cards.md` and `TODO.md`.
 *
 * Set Code: LTR
 * Release Date: June 23, 2023
 * Card Count: 291
 */
object LordOfTheRingsSet : MtgSet {

    override val code = "LTR"
    override val displayName = "The Lord of the Rings: Tales of Middle-earth"
    override val releaseDate = "2023-06-23"
    override val sealedSupported = true

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val basicLands: List<CardDefinition> by lazy {
        CardDiscovery.findBasicLandsIn(CARDS_PACKAGE).map { it.copy(setCode = code) }
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.ltr.cards"
}
