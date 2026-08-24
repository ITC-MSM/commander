package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Scragnoth
 * {4}{G}
 * Creature — Beast
 * 3/4
 * This spell can't be countered.
 * Protection from blue
 */
val Scragnoth = card("Scragnoth") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 4
    oracleText = "This spell can't be countered.\n" +
        "Protection from blue"

    cantBeCountered = true

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLUE)))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "253"
        artist = "Jeff Laubenstein"
        flavorText = "It possesses no intelligence, only counter-intelligence."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d80f7fa7-e7c4-4fc4-99bf-8a8502965fc8.jpg"
    }
}
