package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ghazbán Ogre
 * {G}
 * Creature — Ogre
 * 2/2
 * At the beginning of your upkeep, if a player has more life than each other player,
 * the player with the most life gains control of this creature.
 */
val GhazbanOgre = card("Ghazbán Ogre") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Ogre"
    power = 2
    toughness = 2
    oracleText = "At the beginning of your upkeep, if a player has more life than each other player, the player with the most life gains control of this creature."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.GainControlByMostLife()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Jesper Myrfors"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9d613d5-36a2-4633-b5af-64511bb29cc2.jpg?1562941972"
    }
}
