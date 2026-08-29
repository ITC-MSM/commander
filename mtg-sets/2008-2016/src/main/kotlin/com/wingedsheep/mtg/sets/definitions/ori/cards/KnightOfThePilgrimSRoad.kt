package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Knight of the Pilgrim's Road
 * {2}{W}
 * Creature — Human Knight
 * 3/2
 *
 * Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)
 */
val KnightOfThePilgrimSRoad = card("Knight of the Pilgrim's Road") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)"
    power = 3
    toughness = 2

    keywordAbility(KeywordAbility.renown(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "David Gaillet"
        flavorText = "\"To be a knight, Gideon, is to be the shield for the meek against the cruel.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9de5f39-b07a-4272-8992-ed971132c9c4.jpg"
    }
}
