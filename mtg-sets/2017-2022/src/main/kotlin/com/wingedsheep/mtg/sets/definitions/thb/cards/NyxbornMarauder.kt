package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nyxborn Marauder
 * {2}{B}{B}
 * Enchantment Creature — Minotaur
 * 4/3
 *
 * Vanilla — no rules text.
 */
val NyxbornMarauder = card("Nyxborn Marauder") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment Creature — Minotaur"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Steve Prescott"
        flavorText = "\"Callaphe guided them into the\ndarkness of Hetos, the bleak mire;\nBlood-horned minotaurs circled them,\naxes aglimmer in shadow.\"\n—*The Callapheia*"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd2a923a-1f9c-4a29-9c6b-344ae4d5ae8f.jpg?1783931562"
    }
}
