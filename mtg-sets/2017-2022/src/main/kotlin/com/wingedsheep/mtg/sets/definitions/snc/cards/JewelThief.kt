package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Jewel Thief
 * {2}{G}
 * Creature — Cat Rogue
 * 3 / 3
 * Vigilance, trample
 * When this creature enters, create a Treasure token. (It's an artifact with "{T}, Sacrifice this token: Add one mana of any color.")
 *
 * Two simple keywords plus the shared enters trigger over [Effects.CreateTreasure] — the same
 * predefined-token shape Ticket Tortoise uses, without the intervening-if.
 */
val JewelThief = card("Jewel Thief") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat Rogue"
    oracleText = "Vigilance, trample\nWhen this creature enters, create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"
    power = 3
    toughness = 3

    keywords(Keyword.VIGILANCE, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateTreasure(1)
        description = "When this creature enters, create a Treasure token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "151"
        artist = "Joe Slucher"
        flavorText = "\"They can afford the loss. They build it into their prices.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/3/736e498e-1245-40c1-96a4-c9bcfd1cfe1f.jpg?1783923101"
    }
}
