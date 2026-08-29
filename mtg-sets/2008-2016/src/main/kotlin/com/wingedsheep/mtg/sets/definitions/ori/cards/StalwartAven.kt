package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Stalwart Aven
 * {2}{W}
 * Creature — Bird Soldier
 * 1/3
 *
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 * Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)
 */
val StalwartAven = card("Stalwart Aven") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Soldier"
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)"
    power = 1
    toughness = 3

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.renown(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "32"
        artist = "Scott Murphy"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc4dccbe-877a-4c1e-b46c-711d7c45a506.jpg"
    }
}
