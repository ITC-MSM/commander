package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Shambling Ghoul
 * {1}{B}
 * Creature — Zombie
 * 2/3
 * This creature enters tapped.
 */
val ShamblingGhoul = card("Shambling Ghoul") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 3
    oracleText = "This creature enters tapped."

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Joseph Meehan"
        flavorText = "Once he gets up and going, he will forever shamble on."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd8061d2-84ce-4e4a-9911-ffc9833749da.jpg?1783938337"
    }
}
