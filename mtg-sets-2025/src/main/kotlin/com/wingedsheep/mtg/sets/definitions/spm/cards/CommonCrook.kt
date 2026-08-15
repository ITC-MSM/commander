package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Common Crook
 * {1}{B}
 * Creature — Human Rogue Villain
 * 2/2
 * When this creature dies, create a Treasure token. (It's an artifact with "{T}, Sacrifice this token: Add one mana of any color.")
 */
val CommonCrook = card("Common Crook") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rogue Villain"
    oracleText = "When this creature dies, create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")"
    power = 2
    toughness = 2
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateTreasure()
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "53"
        artist = "Ben Harvey"
        flavorText = "\"Just taking that money out for a walk?\"\n—Spider-Man"
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f5872df-e692-44aa-b18d-22447f5f274c.jpg?1757377079"
    }
}
