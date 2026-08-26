package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Nimbus Wings
 * {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+2 and has flying.
 *
 * A filter-less [ModifyStats]/[GrantKeyword] on an Aura defaults to the enchanted permanent.
 */
val NimbusWings = card("Nimbus Wings") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+2 and has flying."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Chris Rahn"
        flavorText = "Explorers may find ways into the Sky Ruin, but they find its secrets well protected by shifting hedrons and Roil winds."
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5dae4042-8806-437c-8fc1-2d6996ff38c6.jpg"
    }
}
