package com.wingedsheep.mtg.sets.definitions.scg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Dragonstalker
 * {4}{W}
 * Creature — Bird Soldier
 * 3/3
 * Flying
 * Protection from Dragons
 */
val Dragonstalker = card("Dragonstalker") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Soldier"
    power = 3
    toughness = 3
    oracleText = "Flying\nProtection from Dragons"

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Subtype("Dragon")))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Daren Bader"
        flavorText = "It hunts the hunters of the skies."
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58017ff1-74d2-4be2-976a-8dff53e16150.jpg?1562529448"
    }
}
