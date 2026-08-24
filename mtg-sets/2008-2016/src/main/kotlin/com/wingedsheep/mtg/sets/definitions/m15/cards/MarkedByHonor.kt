package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Marked by Honor
 * {3}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+2 and has vigilance.
 */
val MarkedByHonor = card("Marked by Honor") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText =
        "Enchant creature\n" +
        "Enchanted creature gets +2/+2 and has vigilance. (Attacking doesn't cause it to tap.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(+2, +2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "David Palumbo"
        flavorText = "Stand your post for duty. Stand your ground for honor."
        imageUri = "https://cards.scryfall.io/normal/front/d/b/dbc7e2c3-6de6-453d-ade6-8571e7d53df3.jpg?1783939201"
    }
}
