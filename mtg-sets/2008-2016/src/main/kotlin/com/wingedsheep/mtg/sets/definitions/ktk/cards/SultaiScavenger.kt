package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sultai Scavenger
 * {5}{B}
 * Creature — Bird Warrior
 * 3/3
 * Delve
 * Flying
 */
val SultaiScavenger = card("Sultai Scavenger") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Bird Warrior"
    power = 3
    toughness = 3
    oracleText = "Delve (Each card you exile from your graveyard while casting this spell pays for {1}.)\nFlying"

    keywords(Keyword.DELVE, Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Anthony Palumbo"
        flavorText = "Since they guard armies of walking carrion, Sultai aven are never far from a meal."
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cb8e423-f7e7-4ac3-acc2-2a3722e409e7.jpg?1562784280"
    }
}
