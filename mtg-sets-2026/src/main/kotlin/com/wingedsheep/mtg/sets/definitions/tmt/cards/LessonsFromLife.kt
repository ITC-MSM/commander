package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Lessons from Life
 * {2}{G}{U}
 * Sorcery
 *
 * Draw three cards. You may put a land card from your hand onto the
 * battlefield tapped.
 */
val LessonsFromLife = card("Lessons from Life") {
    manaCost = "{2}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Sorcery"
    oracleText = "Draw three cards. You may put a land card from your hand onto the battlefield tapped."

    spell {
        effect = Effects.DrawCards(3)
            .then(Patterns.Hand.putFromHand(filter = GameObjectFilter.Land, entersTapped = true))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Kevin Sidharta"
        flavorText = "\"Everyone's got their own strengths, Lita. You never know what cards they might play. Instead, focus on what you know and never doubt your choices.\"\n—Michelangelo"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f886117-aff3-4db7-9cf5-7cbe94c8cd02.jpg?1771424719"
    }
}
