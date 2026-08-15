package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Arcane Flight
 * {U}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+1 and has flying.
 */
val ArcaneFlight = card("Arcane Flight") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets +1/+1 and has flying."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "43"
        artist = "Steve Prescott"
        flavorText = "\"The Tolarian Academies are known for their magical research, powerful sorcerers, and accidental destruction of ecosystems.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/9/09fbb1c0-ba57-4a5a-8ad6-77fbc6aeeec9.jpg?1562731106"
    }
}
