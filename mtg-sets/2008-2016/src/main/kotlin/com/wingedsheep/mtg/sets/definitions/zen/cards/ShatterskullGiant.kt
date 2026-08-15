package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shatterskull Giant
 * {2}{R}{R}
 * Creature — Giant Warrior
 * 4/3
 *
 * Vanilla — no rules text.
 */
val ShatterskullGiant = card("Shatterskull Giant") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 4
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Kekai Kotaki"
        flavorText = "\"Now I know why they call it Shatterskull Pass. Rocks fell all night like hammers smashing an anvil. It's no wonder the giants are angry all the time.\"\n—Mitra, Bala Ged missionary"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9cfbb4dd-56a2-4a94-9b15-b3ef8b2f1d0b.jpg?1783942140"
    }
}
