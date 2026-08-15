package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Yavimaya Barbarian
 * {R}{G}
 * Creature — Elf Barbarian
 * 2/2
 * Protection from blue
 */
val YavimayaBarbarian = card("Yavimaya Barbarian") {
    manaCost = "{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Elf Barbarian"
    power = 2
    toughness = 2
    oracleText = "Protection from blue"

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLUE)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "290"
        artist = "Don Hazeltine"
        flavorText = "Not all elves embrace the pastoral life. Some still roam the forest's edge, forever making war against their hated enemies."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e17377d-4dad-4144-b0ce-c849636096a2.jpg?1562923706"
    }
}
