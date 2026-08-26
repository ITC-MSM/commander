package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Slither Blade
 * {U}
 * Creature — Snake Rogue
 * 1/2
 * This creature can't be blocked.
 */
val SlitherBlade = card("Slither Blade") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Snake Rogue"
    oracleText = "This creature can't be blocked."
    power = 1
    toughness = 2

    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Zezhou Chen"
        flavorText = "Some naga initiates move as silently the suns' reflections on the water."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63cf067e-4d76-4676-85fa-ebfb0755440a.jpg?1783936514"
    }
}
