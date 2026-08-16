package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * One With the Wind
 * {1}{U}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+2 and has flying.
 */
val OneWithTheWind = card("One With the Wind") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets +2/+2 and has flying."
    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "64"
        artist = "Naomi Baker"
        flavorText = "\"River and sea, jungle and sky. Water flows freely between the two halves of the world. We are creatures of the water.\" —Shaper Tuvasa"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c00f8bf-0088-44ad-b40b-26a2951a2428.jpg?1783935779"
    }
}
