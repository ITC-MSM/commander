package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Haze of Pollen
 * {1}{G}
 * Instant
 * Prevent all combat damage that would be dealt this turn.
 * Cycling {3} ({3}, Discard this card: Draw a card.)
 */
val HazeOfPollen = card("Haze of Pollen") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt this turn.\n" +
            "Cycling {3} ({3}, Discard this card: Draw a card.)"

    spell {
        effect = Effects.PreventAllCombatDamage()
    }

    keywordAbility(KeywordAbility.cycling("{3}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "171"
        artist = "Mark Zug"
        flavorText = "Few can overcome an assault of such aggressive serenity."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b636925d-1c32-4fdc-b814-e6111393d04e.jpg?1783936475"
    }
}
