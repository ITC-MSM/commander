package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Borderland Minotaur
 * {2}{R}{R}
 * Creature — Minotaur Warrior
 * 4/3
 *
 * Vanilla — no rules text.
 */
val BorderlandMinotaur = card("Borderland Minotaur") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Warrior"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "114"
        artist = "Greg Staples"
        flavorText = "\"You have led us to triumph over the forces of Mogis!\" said Brygus the Brave, clapping the Champion on the back. The Champion wiped the sweat and blood from her brow. \"I count eight graves,\" she said. \"Too many to call this a victory.\"\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c7f6eb2-feae-4dc9-a009-0ad60f89a592.jpg?1783939766"
    }
}
