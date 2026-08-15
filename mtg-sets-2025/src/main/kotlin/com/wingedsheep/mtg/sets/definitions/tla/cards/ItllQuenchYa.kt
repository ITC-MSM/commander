package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * It'll Quench Ya!
 * {1}{U}
 * Instant — Lesson
 * Counter target spell unless its controller pays {2}.
 */
val ItllQuenchYa = card("It'll Quench Ya!") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant — Lesson"
    oracleText = "Counter target spell unless its controller pays {2}."

    spell {
        target = Targets.Spell
        effect = Effects.CounterUnlessPays("{2}")
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "Nathaniel Himawan"
        flavorText = "\"Drink cactus juice. Nothing's quenchier. It's the quenchiest!\""
        imageUri = "https://cards.scryfall.io/normal/front/4/7/47c25e41-f43c-4447-81b5-b9631448bd29.jpg?1764120308"
    }
}
