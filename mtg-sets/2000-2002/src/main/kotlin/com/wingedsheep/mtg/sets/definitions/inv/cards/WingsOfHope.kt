package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Wings of Hope
 * {W}{U}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+3 and has flying.
 */
val WingsOfHope = card("Wings of Hope") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets +1/+3 and has flying."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 3)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "289"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be0d2402-f1ef-4a71-ac01-c7099c4ce54c.jpg?1562933234"
    }
}
