package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Onakke Ogre
 * {2}{R}
 * Creature — Ogre Warrior
 * 4/2
 *
 * Vanilla — no rules text.
 */
val OnakkeOgre = card("Onakke Ogre") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Ogre Warrior"
    power = 4
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Mathias Kollros"
        flavorText = "The ogres you know are nothing like the Onakke. Possessing both intellect and industry, they had brute strength without being brutish."
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e016da6-8800-47b4-9b96-1887677c795c.jpg?1783934548"
    }
}
