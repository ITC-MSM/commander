package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Time Stop
 * {4}{U}{U}
 * Instant
 *
 * End the turn. (Exile all spells and abilities, including this spell. The player whose
 * turn it is discards down to their maximum hand size. Damage heals and "this turn" and
 * "until end of turn" effects end.)
 *
 * Takes no target — [Effects.EndTheTurn] always ends the active player's turn (CR 720).
 * When it resolves the whole stack is exiled (including this spell and any triggers the
 * resolution queued, even uncounterable ones), creatures leave combat, and the game skips
 * straight to the cleanup step before the next turn begins.
 */
val TimeStop = card("Time Stop") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "End the turn. (Exile all spells and abilities, including this spell. " +
        "The player whose turn it is discards down to their maximum hand size. Damage " +
        "heals and \"this turn\" and \"until end of turn\" effects end.)"

    spell {
        effect = Effects.EndTheTurn
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "97"
        artist = "Scott M. Fischer"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f968c5e9-12a8-4542-90b4-84e0238fa375.jpg?1783944320"
    }
}
