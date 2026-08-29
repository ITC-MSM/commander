package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Firefiend Elemental
 * {3}{R}
 * Creature — Elemental
 * 3/2
 *
 * Haste (This creature can attack and {T} as soon as it comes under your control.)
 * Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)
 */
val FirefiendElemental = card("Firefiend Elemental") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    oracleText = "Haste (This creature can attack and {T} as soon as it comes under your control.)\n" +
        "Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)"
    power = 3
    toughness = 2

    keywords(Keyword.HASTE)
    keywordAbility(KeywordAbility.renown(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Torstein Nordstrand"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55051412-8749-4b65-ac76-c20ef57fd2e3.jpg"
    }
}
