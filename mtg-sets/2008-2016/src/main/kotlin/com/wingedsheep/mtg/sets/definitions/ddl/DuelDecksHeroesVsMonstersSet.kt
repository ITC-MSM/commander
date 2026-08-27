package com.wingedsheep.mtg.sets.definitions.ddl

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Duel Decks: Heroes vs. Monsters (2013)
 *
 * Scaffolded so that Satyr Hedonist — printed here on 2013-09-06, three weeks before Theros —
 * can hold its canonical [CardDefinition] in its earliest real printing, with a [Printing] row
 * in THS. The rest of the product is still unauthored.
 *
 * [sealedSupported] stays false: a duel deck is two fixed 60-card decks, not a draftable product.
 *
 * Set Code: DDL
 * Release Date: 2013-09-06
 */
object DuelDecksHeroesVsMonstersSet : MtgSet {

    override val code = "DDL"
    override val displayName = "Duel Decks: Heroes vs. Monsters"
    override val releaseDate = "2013-09-06"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.ddl.cards"
}
