package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility


/**
 * Resentful Revelation
 * {1}{B}
 * Sorcery
 * Look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard.
 * Flashback {6}{B} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 */
val ResentfulRevelation = card("Resentful Revelation") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Look at the top three cards of your library. Put one of them into your hand and the rest into your graveyard.\nFlashback {6}{B} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"
    spell {
        effect = Patterns.Library.lookAtTopAndKeep(count = 3, keepCount = 1)
    }
    keywordAbility(KeywordAbility.flashback("{6}{B}"))
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Justyna Dura"
        flavorText = "\"The Jenova Project resulted in my conception.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/4/945006ea-c6a1-4ee5-abb2-387c2b6d3123.jpg"
    }
}
