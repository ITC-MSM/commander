package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stormfront Pegasus
 * {1}{W}
 * Creature — Pegasus
 * 2/1
 *
 * Flying
 *
 * A vanilla flier. The canonical lives here, in Magic 2010 — its earliest real printing —
 * and the later sets carry Printing rows.
 */
val StormfrontPegasus = card("Stormfront Pegasus") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Pegasus"
    power = 2
    toughness = 1
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "rk post"
        flavorText = "\"At summer's end, the pegasus herd stampedes across the sky. Their silent footfalls taunt the clouds and bid the rains to come.\"\n" +
            "—Stormfront fable"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2429a15-ccbe-463c-9218-968709d9e878.jpg?1783942397"
    }
}
