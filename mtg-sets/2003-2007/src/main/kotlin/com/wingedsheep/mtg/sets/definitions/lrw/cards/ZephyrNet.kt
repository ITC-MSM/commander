package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Zephyr Net
 * {1}{U}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature has defender and flying.
 */
val ZephyrNet = card("Zephyr Net") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature has defender and flying."

    auraTarget = Targets.Creature

    // Both grants land in the same layer (6), so they need no CR 613.6 bundling.
    staticAbility {
        ability = GrantKeyword(Keyword.DEFENDER)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Heather Hudson"
        flavorText = "Faeries hang beings that interest them as ornaments in the sky, each clique competing to outshine the prize of the last."
        imageUri = "https://cards.scryfall.io/normal/front/8/1/817b8996-388f-4704-a4c8-ef99d4701805.jpg?1783942894"
    }
}
