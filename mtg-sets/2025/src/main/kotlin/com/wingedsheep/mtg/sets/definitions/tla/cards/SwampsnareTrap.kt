package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Swampsnare Trap
 * {2}{B}
 * Enchantment — Aura
 * This spell costs {1} less to cast if it targets a creature with flying.
 * Enchant creature
 * Enchanted creature gets -5/-3.
 */
val SwampsnareTrap = card("Swampsnare Trap") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "This spell costs {1} less to cast if it targets a creature with flying.\n" +
        "Enchant creature\n" +
        "Enchanted creature gets -5/-3."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfAnyTargetMatches(
                    amount = 1,
                    filter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING)
                )
            )
        )
    }

    staticAbility {
        ability = ModifyStats(-5, -3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "Yoshioka"
        flavorText = "\"Just a little closer. Nice and easy. Nothing to worry about. We just fixin' to eat ya.\"\n—Due, Foggy Swamp Tribe member"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/6348b6bf-08a7-45f0-8b2d-2827ab89f21c.jpg?1764120830"
    }
}
