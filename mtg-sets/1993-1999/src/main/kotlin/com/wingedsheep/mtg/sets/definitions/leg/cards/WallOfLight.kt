package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Wall of Light
 * {2}{W}
 * Creature — Wall
 * 1/5
 *
 * Defender (This creature can't attack.)
 * Protection from black
 */
val WallOfLight = card("Wall of Light") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Wall"
    power = 1
    toughness = 5
    oracleText = "Defender (This creature can't attack.)\nProtection from black"

    keywords(Keyword.DEFENDER)
    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.BLACK)))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Richard Thomas"
        flavorText = "As many attackers were dazzled by the wall's beauty as were halted by its force."
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5758e82-f901-42b7-b705-0e68ca7ba59e.jpg?1783948079"
    }
}
