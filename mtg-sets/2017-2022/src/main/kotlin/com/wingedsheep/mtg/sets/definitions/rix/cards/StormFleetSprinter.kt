package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Storm Fleet Sprinter
 * {1}{U}{R}
 * Creature — Human Pirate
 * 2/2
 * Haste
 * This creature can't be blocked.
 */
val StormFleetSprinter = card("Storm Fleet Sprinter") {
    manaCost = "{1}{U}{R}"
    colorIdentity = "RU"
    typeLine = "Creature — Human Pirate"
    oracleText = "Haste\nThis creature can't be blocked."
    power = 2
    toughness = 2

    keywords(Keyword.HASTE)
    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "172"
        artist = "G-host Lee"
        flavorText = "\"Charge like a red-hot cannonball straight to your target. You slow down, you sink.\"\n—Captain Lannery Storm"
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41ad8525-7618-4c2b-8037-46f53dd42ee0.jpg?1783935268"
    }
}
