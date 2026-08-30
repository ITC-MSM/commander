package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dawnstrike Paladin
 * {3}{W}{W}
 * Creature — Human Knight
 * 2 / 4
 * Vigilance (Attacking doesn't cause this creature to tap.)
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 */
val DawnstrikePaladin = card("Dawnstrike Paladin") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 4
    oracleText = "Vigilance (Attacking doesn't cause this creature to tap.)\n" +
            "Lifelink (Damage dealt by this creature also causes you to gain that much life.)"

    keywords(Keyword.VIGILANCE, Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Tyler Jacobson"
        flavorText = "She crushes darkness beneath her charger's hooves."
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93cf5fb3-bb41-4efa-9721-2c2d169b05cd.jpg"
    }
}
