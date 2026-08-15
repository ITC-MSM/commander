package com.wingedsheep.mtg.sets.definitions.wth.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Disrupt
 * {U}
 * Instant
 *
 * Counter target instant or sorcery spell unless its controller pays {1}.
 * Draw a card.
 *
 * Weatherlight is Disrupt's earliest real-expansion printing; the Invasion
 * printing is a reprint row (see inv/cards/DisruptReprint.kt).
 */
val Disrupt = card("Disrupt") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target instant or sorcery spell unless its controller pays {1}.\nDraw a card."

    spell {
        target = Targets.InstantOrSorcerySpell
        effect = Effects.CounterUnlessPays("{1}") then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Adam Rex"
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6cc89b0-9acf-452b-ac1a-bc7e90eb32fc.jpg?1562803281"
    }
}
