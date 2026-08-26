package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Caravan Hurda
 * {4}{W}
 * Creature — Giant
 * 1/5
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 */
val CaravanHurda = card("Caravan Hurda") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Giant"
    power = 1
    toughness = 5
    oracleText = "Lifelink (Damage dealt by this creature also causes you to gain that much life.)"

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "5"
        artist = "Dave Kendall"
        flavorText = "\"Not too bright, but good enough for the job required—carrying and walking in a straight line.\"\n—Bruse Tarl, Goma Fada nomad"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ffdf3a3d-292d-40b9-b28c-34ad33a76bb4.jpg"
    }
}
