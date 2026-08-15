package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Lórien Revealed
 * {3}{U}{U}
 * Sorcery
 *
 * Draw three cards.
 * Islandcycling {1} ({1}, Discard this card: Search your library for an Island card, reveal it, put it into your hand, then shuffle.)
 */
val LorienRevealed = card("Lórien Revealed") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw three cards.\n" +
        "Islandcycling {1} ({1}, Discard this card: Search your library for an Island card, reveal it, put it into your hand, then shuffle.)"

    spell {
        effect = Effects.DrawCards(3)
    }

    keywordAbility(KeywordAbility.typecycling("Island", ManaCost.parse("{1}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Randy Gallegos"
        flavorText = "\"Look on us now with friendly eyes! Behold the trees of the Naith of Lórien and be glad!\"\n—Haldir"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0ce44270-a684-4489-9077-521456e6dfaa.jpg?1687210977"
    }
}
