package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodrage Brawler
 * {1}{R}
 * Creature — Minotaur Warrior
 * 4/3
 *
 * When this creature enters, discard a card.
 */
val BloodrageBrawler = card("Bloodrage Brawler") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Warrior"
    oracleText = "When this creature enters, discard a card."
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Discard(1)
        description = "When this creature enters, discard a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "121"
        artist = "Lars Grant-West"
        flavorText = "To Hazoret, those who fight for her are her beloved children."
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6080f9e-2415-4e05-97a1-0fe4ad4fdf3b.jpg?1783936492"
    }
}
