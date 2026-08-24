package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Silverbeak Griffin
 * {W}{W}
 * Creature — Griffin
 * 2/2
 * Flying (This creature can't be blocked except by creatures with flying or reach.)
 */
val SilverbeakGriffin = card("Silverbeak Griffin") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    power = 2
    toughness = 2
    oracleText = "Flying (This creature can't be blocked except by creatures with flying or reach.)"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "285"
        artist = "Viktor Titov"
        flavorText = "A pair of domesticated griffins escaped from captivity a century ago. Now the skies are filled with the songs of their descendants."
        imageUri = "https://cards.scryfall.io/normal/front/3/6/36b4c374-42a4-4912-8a74-a11c3fa0e065.jpg"
    }
}
