package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect


/**
 * Dreams of Laguna
 * {1}{U}
 * Instant
 * Surveil 1, then draw a card. (To surveil 1, look at the top card of your library. You may put it into your graveyard.)
 * Flashback {3}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 */
val DreamsOfLaguna = card("Dreams of Laguna") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Surveil 1, then draw a card. (To surveil 1, look at the top card of your library. You may put it into your graveyard.)\nFlashback {3}{U} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"
    spell {
        effect = Effects.Composite(
            Patterns.Library.surveil(1),
            DrawCardsEffect(1)
        )
    }
    keywordAbility(KeywordAbility.flashback("{3}{U}"))
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Solan"
        flavorText = "\"Don't go back on your word. C'mon, go wave to her.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba752243-2727-4b8a-8e21-e70becfd4ff3.jpg"
    }
}
