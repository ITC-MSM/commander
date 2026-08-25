package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mist-Cloaked Herald
 * {U}
 * Creature — Merfolk Warrior
 * 1/1
 * This creature can't be blocked.
 */
val MistCloakedHerald = card("Mist-Cloaked Herald") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Warrior"
    power = 1
    toughness = 1
    oracleText = "This creature can't be blocked."

    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Anthony Palumbo"
        flavorText = "With matchless stealth, the River Heralds fought a running battle against the three enemy forces."
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18c1368e-114b-4618-922b-1d824ba0d1d5.jpg"
    }
}
