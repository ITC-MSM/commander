package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vernadi Shieldmate
 * {1}{G/W}
 * Creature — Human Soldier
 * 2/2
 * Vigilance
 */
val VernadiShieldmate = card("Vernadi Shieldmate") {
    manaCost = "{1}{G/W}"
    colorIdentity = "GW"
    typeLine = "Creature — Human Soldier"
    oracleText = "Vigilance"
    power = 2
    toughness = 2

    keywords(Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "219"
        artist = "Matt Stewart"
        flavorText = "\"Selesnya's soil is sacred, and you're about to meet it with your face.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efddad21-553e-4947-80d2-833b42c45f77.jpg?1783934114"
    }
}
