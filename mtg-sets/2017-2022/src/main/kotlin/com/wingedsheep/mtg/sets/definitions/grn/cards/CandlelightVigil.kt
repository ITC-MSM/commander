package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Candlelight Vigil
 * {3}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +3/+2 and has vigilance.
 */
val CandlelightVigil = card("Candlelight Vigil") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +3/+2 and has vigilance."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(3, 2)
    }
    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Alexander Forssberg"
        flavorText = "Selesnya guildmages do not sleep so the rest of the Conclave can."
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e920a75f-7dec-4815-a358-e174401da83b.jpg?1783934204"
    }
}
