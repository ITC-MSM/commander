package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Soltari Monk
 * {W}{W}
 * Creature — Soltari Monk Cleric
 * 2/1
 * Protection from black
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 */
val SoltariMonk = card("Soltari Monk") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Soltari Monk Cleric"
    power = 2
    toughness = 1
    oracleText = "Protection from black\n" +
        "Shadow (This creature can block or be blocked by only creatures with shadow.)"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLACK)))
    keywords(Keyword.SHADOW)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Janet Aulisio"
        flavorText = "\"Prayer rarely explains.\"\n" +
            "—Orim, Samite healer"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54e0d969-3e4d-4ff9-8bda-3a6ac8df01b2.jpg"
    }
}
