package com.wingedsheep.mtg.sets.definitions.ddf

import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing

/**
 * Duel Decks: Elspeth vs. Tezzeret (2010)
 *
 * Scaffolded so that Kemba's Skyguard — printed here on 2010-09-03, a month before Scars of
 * Mirrodin — can hold its canonical [CardDefinition] in its earliest real printing, with a
 * [Printing] row in SOM. The rest of the product is still unauthored.
 *
 * [sealedSupported] stays false: a duel deck is two fixed 60-card decks, not a draftable product.
 *
 * Set Code: DDF
 * Release Date: 2010-09-03
 */
object DuelDecksElspethVsTezzeretSet : MtgSet {

    override val code = "DDF"
    override val displayName = "Duel Decks: Elspeth vs. Tezzeret"
    override val releaseDate = "2010-09-03"
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

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.ddf.cards"
}
