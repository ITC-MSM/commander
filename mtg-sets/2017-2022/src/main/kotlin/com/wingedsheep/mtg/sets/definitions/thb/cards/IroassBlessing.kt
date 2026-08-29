package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Iroas's Blessing
 * {3}{R}
 * Enchantment — Aura
 *
 * Enchant creature you control
 * When this Aura enters, it deals 4 damage to target creature or planeswalker an opponent controls.
 * Enchanted creature gets +1/+1.
 *
 * Two independent target restrictions: the aura's own is "creature you control", while the
 * enters-the-battlefield trigger reaches across the table for a creature *or planeswalker* an opponent
 * controls — a combined-type object filter rather than a plain creature requirement.
 */
val IroassBlessing = card("Iroas's Blessing") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature you control\n" +
        "When this Aura enters, it deals 4 damage to target creature or planeswalker an opponent controls.\n" +
        "Enchanted creature gets +1/+1."

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.opponentControls()))
        )
        effect = Effects.DealDamage(4, t)
    }

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Victor Adame Minguez"
        flavorText = "The blessing is not the strength itself, but the knowledge of how to use it."
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab6e99a5-b105-489e-aff6-7d2ca50d8ba9.jpg"
    }
}
