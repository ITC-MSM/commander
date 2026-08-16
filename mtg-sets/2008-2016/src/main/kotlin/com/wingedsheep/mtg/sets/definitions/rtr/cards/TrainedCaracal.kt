package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Trained Caracal
 * {W}
 * Creature — Cat
 * 1/1
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 */
val TrainedCaracal = card("Trained Caracal") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 1
    toughness = 1
    oracleText = "Lifelink (Damage dealt by this creature also causes you to gain that much life.)"

    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "James Ryman"
        flavorText = "Some Ravnicans consider carrying a sword to be beneath them, preferring instead a tooth-and-claw escort."
        imageUri = "https://cards.scryfall.io/normal/front/7/9/797e45d1-d17d-40c0-bfdf-ec533784e676.jpg?1783940372"
    }
}
