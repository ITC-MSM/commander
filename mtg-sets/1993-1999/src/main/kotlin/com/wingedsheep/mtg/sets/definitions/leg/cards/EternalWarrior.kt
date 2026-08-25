package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Eternal Warrior
 * {R}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has vigilance.
 */
val EternalWarrior = card("Eternal Warrior") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature has vigilance."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "144"
        artist = "Anson Maddocks"
        flavorText = "Warriors of the Tsunami-nito School spend years in training to master the way of effortless " +
            "effort."
        imageUri = "https://cards.scryfall.io/normal/front/9/7/97cdc38e-1d96-4de2-98e2-713f5d4d2180.jpg?1783948056"
    }
}
