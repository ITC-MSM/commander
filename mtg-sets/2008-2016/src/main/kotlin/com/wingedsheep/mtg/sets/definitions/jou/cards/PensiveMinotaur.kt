package com.wingedsheep.mtg.sets.definitions.jou.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pensive Minotaur
 * {2}{R}
 * Creature — Minotaur Warrior
 * 2/3
 *
 * Vanilla — no rules text.
 */
val PensiveMinotaur = card("Pensive Minotaur") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Warrior"
    power = 2
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Svetlin Velinov"
        flavorText = "The Champion and her companions marched through the night, but the battle was over before they arrived. In the middle of the carnage sat a solitary minotaur, lost in what seemed to the Champion to be thought.\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/902b462a-a552-42d4-91f0-bd33cd9cb719.jpg?1783939421"
    }
}
