package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Undersea Invader
 * {4}{U}{U}
 * Creature — Giant Rogue
 * 5/6
 * Flash (You may cast this spell any time you could cast an instant.)
 * This creature enters tapped.
 */
val UnderseaInvader = card("Undersea Invader") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Giant Rogue"
    power = 5
    toughness = 6
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\nThis creature enters tapped."

    keywords(Keyword.FLASH)

    // This creature enters tapped.
    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "Lorenzo Mastroianni"
        flavorText = "\"The currents are strange today, and the fish are fearful. Something's stirring in the depths.\" —Rathstaf, Kannah fisherman"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/550c745b-64e8-4d20-9cf0-024248ddbd57.jpg?1783928254"
    }
}
