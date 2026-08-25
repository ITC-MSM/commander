package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cat Warriors
 * {1}{G}{G}
 * Creature — Cat Warrior
 * 2/2
 *
 * Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)
 */
val CatWarriors = card("Cat Warriors") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Warrior"
    power = 2
    toughness = 2
    oracleText = "Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)"

    keywords(Keyword.FORESTWALK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Melissa A. Benson"
        flavorText = "These stealthy felines have survived so many battles that some believe they must possess " +
            "many lives."
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2187a64-2823-4f58-ad35-70f8913db2dc.jpg?1783948049"
    }
}
