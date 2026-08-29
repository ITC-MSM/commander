package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Talas Warrior
 * {1}{U}{U}
 * Creature — Human Pirate Warrior
 * 2/2
 * This creature can't be blocked.
 */
val TalasWarrior = card("Talas Warrior") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Pirate Warrior"
    oracleText = "This creature can't be blocked."
    power = 2
    toughness = 2
    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "53"
        artist = "Douglas Shuler"
        flavorText = "The sea is in their blood. Literally."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de3d8cc5-5889-4e52-a32c-d15556fd2166.jpg"
    }
}
