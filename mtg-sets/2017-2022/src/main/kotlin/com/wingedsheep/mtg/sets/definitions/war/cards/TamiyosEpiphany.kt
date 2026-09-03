package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tamiyo's Epiphany
 * {3}{U}
 * Sorcery
 * Scry 4, then draw two cards.
 *
 * Two steps in printed order — the scry resolves before the draw, so the cards bottomed by the
 * scry can't be drawn back and the ones kept on top are what you draw.
 */
val TamiyosEpiphany = card("Tamiyo's Epiphany") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Scry 4, then draw two cards."

    spell {
        effect = Effects.Composite(
            Effects.Scry(4),
            Effects.DrawCards(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Lake Hurwitz"
        flavorText = "Tamiyo wished only to observe the war, but she soon realized neutrality was not an option."
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce338cf3-46dc-4c87-8df5-af27097c7dd4.jpg"
    }
}
