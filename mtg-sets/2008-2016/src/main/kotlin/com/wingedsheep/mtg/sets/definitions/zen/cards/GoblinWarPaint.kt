package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Goblin War Paint
 * {1}{R}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+2 and has haste.
 */
val GoblinWarPaint = card("Goblin War Paint") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2 and has haste."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Austin Hsu"
        flavorText = "War paint made from kolya fruit heightens senses and lessens fear. Unfortunately, fear is " +
            "usually what keeps you alive."
        imageUri = "https://cards.scryfall.io/normal/front/4/3/4388e57e-0c87-4d66-a862-58261d76c5ac.jpg?1783942144"
    }
}
