package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Immolation
 * {R}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +2/-2.
 */
val Immolation = card("Immolation") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets +2/-2."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, -2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Scott Kirschner"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b3d34fa-398c-4ea0-a392-6690bd3a615c.jpg?1783948055"
    }
}
