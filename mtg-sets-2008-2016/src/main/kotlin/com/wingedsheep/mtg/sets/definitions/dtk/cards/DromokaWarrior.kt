package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dromoka Warrior
 * {1}{W}
 * Creature — Human Warrior
 * 3/1 vanilla
 */
val DromokaWarrior = card("Dromoka Warrior") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Warrior"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Zack Stella"
        flavorText = "Dromoka has regard for the humans who serve under her. In return for her protection, they obey with steadfast loyalty, acting as weapons for her and her scalelords against the other clans."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13ae001b-556f-4576-8cf4-0b8902997bb1.jpg?1562782782"
    }
}
