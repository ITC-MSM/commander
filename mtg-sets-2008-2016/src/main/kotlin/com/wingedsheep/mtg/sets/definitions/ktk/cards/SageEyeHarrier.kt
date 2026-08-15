package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sage-Eye Harrier
 * {4}{W}
 * Creature — Bird Warrior
 * 1/5
 * Flying
 * Morph {3}{W}
 */
val SageEyeHarrier = card("Sage-Eye Harrier") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Warrior"
    power = 1
    toughness = 5
    oracleText = "Flying\nMorph {3}{W}"

    keywords(Keyword.FLYING)
    morph = "{3}{W}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Chase Stone"
        flavorText = "\"These winged warriors meditate in flight, tracing mandalas in the clouds.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3cc7b4c3-647e-48da-86f8-f55e3e990ac1.jpg?1562785176"
    }
}
