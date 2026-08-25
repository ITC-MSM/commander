package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Charging Monstrosaur
 * {4}{R}
 * Creature — Dinosaur
 * 5/5
 *
 * Trample, haste
 */
val ChargingMonstrosaur = card("Charging Monstrosaur") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    oracleText = "Trample, haste"
    power = 5
    toughness = 5

    keywords(Keyword.TRAMPLE, Keyword.HASTE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "138"
        artist = "Zack Stella"
        flavorText = "\"I knew I should have stayed with the boat. Always stay with the boat!\""
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5222448-95d1-4b63-ab76-d5060febcf38.jpg"
    }
}
