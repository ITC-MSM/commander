package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Elemental Summoning — Strixhaven: School of Mages #183 (canonical printing)
 * {3}{U/R}{U/R} · Sorcery — Lesson
 *
 * Create a 4/4 blue and red Elemental creature token.
 *
 * A single [Effects.CreateToken]: a 4/4 blue-and-red Elemental with no keywords. Token art
 * resolves through STX's synced token printings, so none is declared here. Lesson is only a
 * subtype.
 */
val ElementalSummoning = card("Elemental Summoning") {
    manaCost = "{3}{U/R}{U/R}"
    colorIdentity = "RU"
    typeLine = "Sorcery — Lesson"
    oracleText =
        "Create a 4/4 blue and red Elemental creature token."

    spell {
        effect = Effects.CreateToken(
            power = 4,
            toughness = 4,
            colors = setOf(Color.BLUE, Color.RED),
            creatureTypes = setOf("Elemental")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Marta Nael"
        flavorText = "\"You've made a splash. Now show me a torrent.\"\n—Uvilda, Prismari dean"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea51991c-1589-4c62-965b-5ae8d233520b.jpg?1783927316"
    }
}
