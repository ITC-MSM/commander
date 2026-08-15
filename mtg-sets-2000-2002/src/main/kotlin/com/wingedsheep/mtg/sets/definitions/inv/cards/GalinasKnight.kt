package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Galina's Knight
 * {W}{U}
 * Creature — Merfolk Knight
 * 2/2
 * Protection from red
 */
val GalinasKnight = card("Galina's Knight") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Merfolk Knight"
    power = 2
    toughness = 2
    oracleText = "Protection from red"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.RED)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "248"
        artist = "David Martin"
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11b492d6-5e28-4f4b-942c-080d03cb0e92.jpg?1562898560"
    }
}
