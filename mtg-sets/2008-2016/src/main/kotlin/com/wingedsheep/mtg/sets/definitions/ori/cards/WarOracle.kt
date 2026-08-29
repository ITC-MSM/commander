package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * War Oracle
 * {2}{W}{W}
 * Creature — Human Cleric
 * 3/3
 *
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 * Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)
 */
val WarOracle = card("War Oracle") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "Lifelink (Damage dealt by this creature also causes you to gain that much life.)\n" +
        "Renown 1 (When this creature deals combat damage to a player, if it isn't renowned, put a +1/+1 counter on it and it becomes renowned.)"
    power = 3
    toughness = 3

    keywords(Keyword.LIFELINK)
    keywordAbility(KeywordAbility.renown(1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "41"
        artist = "Steve Prescott"
        flavorText = "\"When you are felled by my mace, you shall know it was divine fate.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d8827bf-11c3-4f78-b7aa-ae953442c709.jpg"
    }
}
