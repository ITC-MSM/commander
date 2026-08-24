package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sungrace Pegasus
 * {1}{W}
 * Creature — Pegasus
 * 1/2
 * Flying
 * Lifelink
 */
val SungracePegasus = card("Sungrace Pegasus") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Pegasus"
    power = 1
    toughness = 2
    oracleText =
        "Flying\n" +
        "Lifelink (Damage dealt by this creature also causes you to gain that much life.)"

    keywords(Keyword.FLYING, Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Phill Simmer"
        flavorText = "The sacred feathers of the pegasus are said to have healing powers."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52d851b9-c290-4fcc-860d-a3250923b850.jpg?1783939197"
    }
}
