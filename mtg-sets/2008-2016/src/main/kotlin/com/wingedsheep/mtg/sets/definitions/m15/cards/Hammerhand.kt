package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Hammerhand
 * {R}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, target creature can't block this turn.
 * Enchanted creature gets +1/+1 and has haste.
 *
 * The enters trigger targets independently of the Aura's own enchant target — it is usually
 * pointed at a would-be blocker, not the host.
 */
val Hammerhand = card("Hammerhand") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText =
        "Enchant creature\n" +
        "When this Aura enters, target creature can't block this turn.\n" +
        "Enchanted creature gets +1/+1 and has haste."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target creature", Targets.Creature)
        effect = Effects.CantBlock(t)
        description = "When this Aura enters, target creature can't block this turn."
    }

    staticAbility {
        ability = ModifyStats(+1, +1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Tomasz Jedruszek"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7a81cc6-2660-4501-b589-f8c3a26ee483.jpg?1783939173"
    }
}
