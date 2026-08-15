package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Fishliver Oil
 * {1}{U}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature has islandwalk.
 */
val FishliverOil = card("Fishliver Oil") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature has islandwalk. (It can't be blocked as long as defending player controls an Island.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.ISLANDWALK, Filters.EnchantedCreature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Anson Maddocks"
        flavorText = "Then the maiden bade him cast off his robes and cover his body with fishliver oil, that he might safely follow her into the sea."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/deb6ed87-aa07-4b5e-ac40-1e16dc2a817a.jpg?1562936572"
    }
}
