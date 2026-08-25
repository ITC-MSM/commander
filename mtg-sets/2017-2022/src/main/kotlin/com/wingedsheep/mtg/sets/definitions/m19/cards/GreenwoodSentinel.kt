package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Greenwood Sentinel
 * {1}{G}
 * Creature — Elf Scout
 * 2/2
 * Vigilance (Attacking doesn't cause this creature to tap.)
 */
val GreenwoodSentinel = card("Greenwood Sentinel") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Scout"
    power = 2
    toughness = 2
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)"

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "187"
        artist = "Johann Bodin"
        flavorText = "Within a mile of the woodland, you will feel her eyes upon you. Within its borders, you will feel her blade."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2eebdd18-6930-42e0-b589-fe15820db6e1.jpg"
    }
}
