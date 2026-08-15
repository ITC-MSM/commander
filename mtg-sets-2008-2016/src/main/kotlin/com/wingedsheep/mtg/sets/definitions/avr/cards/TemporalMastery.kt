package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.TakeExtraTurnEffect

/**
 * Temporal Mastery
 * {5}{U}{U}
 * Sorcery
 * Take an extra turn after this one. Exile Temporal Mastery.
 * Miracle {1}{U}
 */
val TemporalMastery = card("Temporal Mastery") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Take an extra turn after this one. Exile Temporal Mastery.\n" +
        "Miracle {1}{U} (You may cast this card for its miracle cost when you draw it if it's the first card you drew this turn.)"

    spell {
        selfExile()
        effect = TakeExtraTurnEffect()
    }

    keywordAbility(KeywordAbility.miracle("{1}{U}"))

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "81"
        artist = "Franz Vohwinkel"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/266e5267-2288-4bb0-8c54-0c556521cec3.jpg?1782714505"
    }
}
