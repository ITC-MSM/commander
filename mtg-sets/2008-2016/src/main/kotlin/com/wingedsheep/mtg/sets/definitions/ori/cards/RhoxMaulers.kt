package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Rhox Maulers
 * {4}{G}
 * Creature — Rhino Soldier
 * 4/4
 *
 * Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)
 * Renown 2 (When this creature deals combat damage to a player, if it isn't renowned, put two +1/+1 counters on it and it becomes renowned.)
 */
val RhoxMaulers = card("Rhox Maulers") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Rhino Soldier"
    oracleText = "Trample (This creature can deal excess combat damage to the player or planeswalker it's attacking.)\n" +
        "Renown 2 (When this creature deals combat damage to a player, if it isn't renowned, put two +1/+1 counters on it and it becomes renowned.)"
    power = 4
    toughness = 4

    keywords(Keyword.TRAMPLE)
    keywordAbility(KeywordAbility.renown(2))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "196"
        artist = "Zoltan Boros"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64c3c972-82f6-46ea-8f9f-090c65c22e44.jpg"
    }
}
