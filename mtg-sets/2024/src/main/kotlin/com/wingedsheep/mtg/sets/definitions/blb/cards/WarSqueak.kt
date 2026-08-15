package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * War Squeak
 * {R}
 * Enchantment — Aura
 *
 * Enchant creature
 * When this Aura enters, target creature an opponent controls can't block this turn.
 * Enchanted creature gets +1/+1 and has haste.
 */
val WarSqueak = card("War Squeak") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, target creature an opponent controls can't block this turn.\n" +
        "Enchanted creature gets +1/+1 and has haste."

    auraTarget = Targets.Creature

    // When this Aura enters, target creature an opponent controls can't block this turn.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = Targets.CreatureOpponentControls
        effect = Effects.CantBlock(EffectTarget.ContextTarget(0))
    }

    // Enchanted creature gets +1/+1
    staticAbility {
        ability = ModifyStats(1, 1)
    }

    // Enchanted creature has haste
    staticAbility {
        ability = GrantKeyword(Keyword.HASTE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "160"
        artist = "Zoltan Boros"
        flavorText = "Dangers sharper than thorns can hide among flowers."
        imageUri = "https://cards.scryfall.io/normal/front/1/0/105964a7-88b7-4340-aa66-e908189a3638.jpg?1721426742"
    }
}
