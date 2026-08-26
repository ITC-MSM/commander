package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Blood Knight
 * {R}{R}
 * Creature — Human Knight
 * 2/2
 * First strike, protection from white
 */
val BloodKnight = card("Blood Knight") {
    manaCost = "{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "First strike, protection from white"

    keywords(Keyword.FIRST_STRIKE)
    keywordAbility(KeywordAbility.protectionFrom(Color.WHITE))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "115"
        artist = "Matt Cavotta"
        flavorText = "His is the fury of the wildfire, the boiling blood of the volcano. He fights you not because you've wronged him, but because you're there."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f2133c0-8561-4264-8802-1b2933abf186.jpg"
    }
}
