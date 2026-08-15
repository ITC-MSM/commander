package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cliffhaven Sell-Sword
 * {1}{W}
 * Creature — Kor Warrior
 * 3/1
 *
 * Vanilla — no rules text.
 */
val CliffhavenSellSword = card("Cliffhaven Sell-Sword") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Warrior"
    power = 3
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Jason Rainville"
        flavorText = "\"I swing a sword. It's a simple life. A lot of adventurers just see me as some cheap muscle, but if my steel lets them return home with their lives and limbs intact, then I've done my job.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f334767-4353-4379-a934-fa67075db439.jpg?1783929421"
    }
}
