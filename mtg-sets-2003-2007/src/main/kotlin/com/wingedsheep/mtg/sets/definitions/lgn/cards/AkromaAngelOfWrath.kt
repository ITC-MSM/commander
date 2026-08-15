package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Akroma, Angel of Wrath
 * {5}{W}{W}{W}
 * Legendary Creature — Angel
 * 6/6
 * Flying, first strike, vigilance, trample, haste, protection from black and from red
 */
val AkromaAngelOfWrath = card("Akroma, Angel of Wrath") {
    manaCost = "{5}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Angel"
    power = 6
    toughness = 6
    oracleText = "Flying, first strike, vigilance, trample, haste, protection from black and from red"

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE, Keyword.VIGILANCE, Keyword.TRAMPLE, Keyword.HASTE)
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLACK)))
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.RED)))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "1"
        artist = "Ron Spears"
        flavorText = "\"No rest. No mercy. No matter what.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/1/814245de-6105-43ef-acbf-d12d304b6331.jpg?1562921034"
    }
}
