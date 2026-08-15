package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.LoseAllAbilities
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Fresh Start — Tarkir: Dragonstorm #46
 * {1}{U} · Enchantment — Aura
 *
 * Flash
 * Enchant creature
 * Enchanted creature gets -5/-0 and loses all abilities.
 */
val FreshStart = card("Fresh Start") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\nEnchant creature\nEnchanted creature gets -5/-0 and loses all abilities."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    // Enchanted creature gets -5/-0 (Layer 7c)...
    staticAbility {
        ability = ModifyStats(-5, 0)
    }

    // ...and loses all abilities (Layer 6).
    staticAbility {
        ability = LoseAllAbilities()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Joe Slucher"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96f7af08-ac05-45d0-979f-282943130c61.jpg?1743204145"
    }
}
