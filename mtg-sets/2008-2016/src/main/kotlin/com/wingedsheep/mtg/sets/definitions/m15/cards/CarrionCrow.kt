package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Carrion Crow
 * {2}{B}
 * Creature — Zombie Bird
 * 2/2
 * Flying
 * This creature enters tapped.
 */
val CarrionCrow = card("Carrion Crow") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Bird"
    power = 2
    toughness = 2
    oracleText =
        "Flying (This creature can't be blocked except by creatures with flying or reach.)\n" +
        "This creature enters tapped."

    keywords(Keyword.FLYING)

    replacementEffect(EntersTapped())

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "Aaron Miller"
        flavorText = "When carrion feeds on carrion, dark days approach."
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97d80cc4-f3be-4306-8126-e60f7b00d384.jpg?1783939186"
    }
}
