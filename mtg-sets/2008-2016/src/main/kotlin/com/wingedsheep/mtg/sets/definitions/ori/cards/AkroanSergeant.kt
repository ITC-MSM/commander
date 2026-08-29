package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Akroan Sergeant
 * {2}{R}
 * Creature — Human Soldier
 * 2/2
 *
 * First strike (This creature deals combat damage before creatures without first strike.)
 * Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)
 */
val AkroanSergeant = card("Akroan Sergeant") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Soldier"
    oracleText = "First strike (This creature deals combat damage before creatures without first strike.)\n" +
        "Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)"
    power = 2
    toughness = 2

    keywords(Keyword.FIRST_STRIKE)
    keywordAbility(KeywordAbility.renown(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Zack Stella"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31913547-460c-45b3-be23-89f0e3a43325.jpg"
    }
}
