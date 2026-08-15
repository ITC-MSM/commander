package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Armadillo Cloak
 * {1}{G}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +2/+2 and has trample.
 * Whenever enchanted creature deals damage, you gain that much life.
 */
val ArmadilloCloak = card("Armadillo Cloak") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +2/+2 and has trample.\n" +
        "Whenever enchanted creature deals damage, you gain that much life."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE)
    }

    triggeredAbility {
        trigger = Triggers.dealsDamage(binding = TriggerBinding.ATTACHED)
        effect = Effects.GainLife(DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "229"
        artist = "Paolo Parente"
        flavorText = "\"Don't laugh. It works.\"\n—Yavimaya ranger"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d816f98-6cb6-432c-b0a4-a0eed21658ac.jpg?1562926718"
    }
}
