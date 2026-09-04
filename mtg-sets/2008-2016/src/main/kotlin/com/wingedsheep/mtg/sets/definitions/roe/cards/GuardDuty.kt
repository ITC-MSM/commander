package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Guard Duty
 * {W}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has defender.
 *
 * Modeling notes:
 *  - "Enchant creature" is the Aura's attachment requirement, authored as
 *    `auraTarget = Targets.Creature` — an Aura with no `auraTarget` never attaches to anything.
 *  - The grant is a *static* ability (`GrantKeyword`), which projects in Layer 6 onto the
 *    attached creature. Its default scope is the enchanted creature, so no explicit filter is
 *    needed — the same shape as Zephyr Net, which grants defender and flying this way.
 *  - Nothing about the grant is conditional or timed, so no `Duration` and no condition: it lasts
 *    exactly as long as the Aura stays attached.
 */
val GuardDuty = card("Guard Duty") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
            "Enchanted creature has defender."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.DEFENDER)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Karl Kopinski"
        flavorText = "\"I was told these were standard issue. Do I look standard to you?\""
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f77efd36-f9e7-4c34-a6ee-9e1c9b273fb7.jpg?1783942008"
    }
}
