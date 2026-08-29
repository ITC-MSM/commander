package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Citadel Castellan
 * {1}{G}{W}
 * Creature — Human Knight
 * 2/3
 *
 * Vigilance (Attacking doesn't cause this creature to tap.)
 * Renown 2 (When this creature deals combat damage to a player, if it isn't renowned, put two +1/+1 counters on it and it becomes renowned.)
 */
val CitadelCastellan = card("Citadel Castellan") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Human Knight"
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)\n" +
        "Renown 2 (When this creature deals combat damage to a player, if it isn't renowned, put two +1/+1 counters on it and it becomes renowned.)"
    power = 2
    toughness = 3

    keywords(Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.renown(2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "213"
        artist = "Anastasia Ovchinnikova"
        flavorText = "\"I am the first line of defense. You will not encounter the second.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9aa11f30-f2b7-4279-9176-c336c74538bf.jpg"
    }
}
