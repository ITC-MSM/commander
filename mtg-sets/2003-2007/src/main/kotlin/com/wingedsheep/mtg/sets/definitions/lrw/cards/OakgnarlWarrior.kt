package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Oakgnarl Warrior
 * {5}{G}{G}
 * Creature — Treefolk Warrior
 * 5/7
 * Vigilance, trample
 */
val OakgnarlWarrior = card("Oakgnarl Warrior") {
    manaCost = "{5}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Warrior"
    power = 5
    toughness = 7
    oracleText = "Vigilance, trample"

    keywords(Keyword.VIGILANCE, Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "232"
        artist = "Jim Nelson"
        flavorText = "\"Roam as you will, your roots remain in the strong earth of your Rising.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/9/09fe818e-6dbc-4100-8552-b0393c87b052.jpg?1783942858"
    }
}
