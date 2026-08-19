package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Feral Invocation
 * {2}{G}
 * Enchantment — Aura
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * Enchant creature
 * Enchanted creature gets +2/+2.
 */
val FeralInvocation = card("Feral Invocation") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\nEnchant creature\nEnchanted creature gets +2/+2."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Mathias Kollros"
        flavorText = "Nylea's sacred lynx guards those who honor the Nessian Wood and hunts those who don't."
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c1f15a2-0058-4188-9696-385fa6974bd4.jpg?1783939745"
    }
}
