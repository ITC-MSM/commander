package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rain of Revelation — Modern Horizons #65
 * {3}{U} · Instant
 *
 * Draw three cards, then discard a card.
 *
 * `Effects.Discard` expands to the Gather → Select → Move(Discard) hand pipeline, so the
 * discard is a real choice made on resolution after the three cards have been drawn.
 */
val RainOfRevelation = card("Rain of Revelation") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Draw three cards, then discard a card."

    spell {
        effect = Effects.DrawCards(3) then Effects.Discard(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Nils Hamm"
        flavorText = "\"As the sky opened up, we ran for shelter. Halfway there I came to the sudden realization that, already soaked, there might be more to gain from experiencing the rain than running from it.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42230b38-c81a-4d76-ad17-1166f5f62312.jpg?1783933138"
    }
}
