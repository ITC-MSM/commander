package com.wingedsheep.mtg.sets.definitions.ons.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Foothill Guide
 * {W}
 * Creature — Human Cleric Soldier
 * 1/1
 * Protection from Goblins
 * Morph {W}
 */
val FoothillGuide = card("Foothill Guide") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "Protection from Goblins\nMorph {W}"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Subtype("Goblin")))
    morph = "{W}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Eric Peterson"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/409adb7b-6dcb-4e7f-a5dd-c0adf12140a4.jpg?1562910177"
    }
}
