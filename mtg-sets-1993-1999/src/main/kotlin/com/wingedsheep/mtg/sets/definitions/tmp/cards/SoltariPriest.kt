package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Soltari Priest
 * {W}{W}
 * Creature — Soltari Cleric
 * 2/1
 * Protection from red
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 */
val SoltariPriest = card("Soltari Priest") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Soltari Cleric"
    power = 2
    toughness = 1
    oracleText = "Protection from red\n" +
        "Shadow (This creature can block or be blocked by only creatures with shadow.)"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.RED)))
    keywords(Keyword.SHADOW)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Janet Aulisio"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35a71390-3fa8-43eb-ad86-67de2a7aeab8.jpg?1562053297"
    }
}
