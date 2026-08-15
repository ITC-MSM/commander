package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Sabertooth Nishoba
 * {4}{G}{W}
 * Creature — Cat Beast Warrior
 * 5/5
 * Trample, protection from blue and from red
 */
val SabertoothNishoba = card("Sabertooth Nishoba") {
    manaCost = "{4}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Cat Beast Warrior"
    power = 5
    toughness = 5
    oracleText = "Trample, protection from blue and from red"

    keywords(Keyword.TRAMPLE)
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLUE)))
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.RED)))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "268"
        artist = "Gary Ruddell"
        flavorText = "They sneer at the terrestrial dangers found on peaks and shores, " +
            "eager to prove themselves against new and even mightier foes."
        imageUri = "https://cards.scryfall.io/normal/front/8/3/8338c296-cf3f-41d7-b380-3fb4237cb41c.jpg?1562921586"
    }
}
