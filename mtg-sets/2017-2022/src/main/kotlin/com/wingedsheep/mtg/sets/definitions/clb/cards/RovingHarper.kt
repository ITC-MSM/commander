package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Roving Harper
 * {2}{W}
 * Creature — Elf Scout
 * 2/2
 * When this creature enters, draw a card.
 *
 * The plainest cantrip body there is: [Triggers.EntersBattlefield] carrying [Effects.DrawCards], whose
 * controller-drawing default is exactly what the printed line means.
 */
val RovingHarper = card("Roving Harper") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elf Scout"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
        description = "When this creature enters, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Anastasia Ovchinnikova"
        flavorText = "The Harpers value peaceful coexistence, historical preservation, and harmony with nature, and each day they spread their ideals a little farther."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6b0ed9c-9a99-4a50-80a9-396420a8dcf9.jpg?1783922804"
    }
}
