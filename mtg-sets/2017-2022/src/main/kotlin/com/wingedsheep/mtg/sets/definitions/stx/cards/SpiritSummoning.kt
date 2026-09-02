package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Spirit Summoning — Strixhaven: School of Mages #236 (canonical printing)
 * {1}{R/W}{R/W} · Sorcery — Lesson
 *
 * Create a 3/2 red and white Spirit creature token.
 *
 * A single [Effects.CreateToken]: a 3/2 red-and-white Spirit with no keywords. Token art resolves
 * through STX's synced token printings, so none is declared here. Lesson is only a subtype.
 */
val SpiritSummoning = card("Spirit Summoning") {
    manaCost = "{1}{R/W}{R/W}"
    colorIdentity = "RW"
    typeLine = "Sorcery — Lesson"
    oracleText =
        "Create a 3/2 red and white Spirit creature token."

    spell {
        effect = Effects.CreateToken(
            power = 3,
            toughness = 2,
            colors = setOf(Color.RED, Color.WHITE),
            creatureTypes = setOf("Spirit")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "236"
        artist = "Andrey Kuzinskiy"
        flavorText = "\"Books are fantastic, but if you're really curious about the distant past, why not ask someone who was there?\"\n—Augusta, Lorehold dean"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74be6236-4095-419c-9927-fbd874df21f8.jpg?1783927289"
    }
}
