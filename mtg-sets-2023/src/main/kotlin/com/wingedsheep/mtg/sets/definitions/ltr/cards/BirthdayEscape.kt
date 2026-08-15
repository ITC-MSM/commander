package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Birthday Escape
 * {U}
 * Sorcery
 *
 * Draw a card. The Ring tempts you.
 */
val BirthdayEscape = card("Birthday Escape") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Draw a card. The Ring tempts you."

    spell {
        effect = Effects.DrawCards(1).then(Effects.TheRingTemptsYou())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "David Álvarez"
        flavorText = "As Bilbo finished his speech, he slipped the ring on his finger, and he was never seen by any Hobbit in Hobbiton again."
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42db2313-b13d-4292-bef2-bf86f989d32f.jpg?1686968028"
    }
}
