package com.wingedsheep.mtg.sets.definitions.big

import com.wingedsheep.mtg.sets.definitions.otj.OutlawsOfThunderJunctionSet
import com.wingedsheep.mtg.sets.discovery.CardDiscovery
import com.wingedsheep.mtg.sets.tokens.TokenArtData
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.MtgSet
import com.wingedsheep.sdk.model.Printing
import com.wingedsheep.sdk.model.TokenPrinting

/**
 * The Big Score (2024) — bonus sheet shipped alongside Outlaws of Thunder Junction.
 *
 * Set Code: BIG
 * Release Date: April 19, 2024
 */
object TheBigScoreSet : MtgSet {

    override val code = "BIG"
    override val displayName = "The Big Score"
    override val releaseDate = "2024-04-19"

    // All 30 cards of the bonus sheet are implemented — surface it as complete, not "partial".
    override val sealedSupported = true

    // A 30-card bonus sheet can't sustain a sealed/draft pool by itself — it is only playable
    // together with at least one regular set.
    override val extensionSet = true

    /**
     * Tokens the bonus sheet mints that `tbig` doesn't print, borrowed from Outlaws of Thunder
     * Junction.
     *
     * The Big Score was opened inside OTJ boosters and shares its token sheet: Scryfall's `tbig`
     * lists only the tokens unique to the bonus sheet, so the Clue and Treasure its cards create
     * live in `totj`. Taking OTJ's whole sheet minus what BIG prints itself means any token a
     * later BIG card mints picks up the right art too, instead of the engine-wide generic.
     *
     * The filter matters because the wiring registers `tokenArt` *ahead* of the set's own synced
     * rows — without it, a token both sheets print would render OTJ's art on a BIG card.
     */
    override val tokenArt: List<TokenPrinting> by lazy {
        val own = TokenArtData.forSet(code)
        TokenArtData.forSet(OutlawsOfThunderJunctionSet.code)
            .filterNot { borrowed -> own.any { it.matchesName(borrowed.name) } }
    }

    override val cards: List<CardDefinition> by lazy {
        CardDiscovery.findIn(CARDS_PACKAGE)
    }

    override val printings: List<Printing> by lazy {
        CardDiscovery.findPrintingsIn(CARDS_PACKAGE)
    }

    private const val CARDS_PACKAGE = "com.wingedsheep.mtg.sets.definitions.big.cards"
}
