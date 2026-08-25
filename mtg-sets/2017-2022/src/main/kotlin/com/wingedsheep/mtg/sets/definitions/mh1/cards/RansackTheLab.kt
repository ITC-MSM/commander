package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ransack the Lab — Modern Horizons #103
 * {1}{B} · Sorcery
 *
 * Look at the top three cards of your library. Put one of them into your hand and the rest
 * into your graveyard.
 *
 * The stock Gather → Select → Move(kept) → Move(rest) library recipe; the hand/graveyard
 * destinations are the pattern's defaults, so the selection labels derive from them.
 */
val RansackTheLab = card("Ransack the Lab") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard."

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(count = 3, keepCount = 1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Chris Seaman"
        flavorText = "\"I think someone is stealing from my laboratory. It had better not be you!\"\n—Geralf, letter to Gisa"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b547513d-8b69-41cd-84c9-4b08b6426f1d.jpg?1783933123"
    }
}
