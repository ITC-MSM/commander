package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Cartouche of Zeal
 * {R}
 * Enchantment — Aura Cartouche
 * Enchant creature you control
 * When this Aura enters, target creature can't block this turn.
 * Enchanted creature gets +1/+1 and has haste. (It can attack and {T} no matter when it came under your control.)
 */
val CartoucheOfZeal = card("Cartouche of Zeal") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura Cartouche"
    oracleText = "Enchant creature you control\n" +
        "When this Aura enters, target creature can't block this turn.\n" +
        "Enchanted creature gets +1/+1 and has haste. (It can attack and {T} no matter when it came under your control.)"

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        effect = Effects.CantBlock(t)
    }

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "124"
        artist = "Kieran Yanner"
        flavorText = "The fifth cartouche is the final affirmation of glory, granted only to the worthy dead."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d09b2d9-b944-480b-93f9-76fc9bc319ce.jpg?1783936492"
    }
}
