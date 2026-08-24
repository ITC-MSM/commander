package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Stoic Rebuttal
 * {1}{U}{U}
 * Instant
 *
 * Metalcraft — This spell costs {1} less to cast if you control three or more artifacts.
 * Counter target spell.
 *
 * "Metalcraft" is an ability word (CR 207.2c) — no keyword, no rules meaning of its own. The
 * discount is an ordinary self-cast [ModifySpellCost] gated by [CostGating.OnlyIf], evaluated as
 * the spell is cast (CR 601.2f).
 */
val StoicRebuttal = card("Stoic Rebuttal") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Metalcraft — This spell costs {1} less to cast if you control three or more artifacts.\n" +
        "Counter target spell."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(1),
            gating = CostGating.OnlyIf(Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact))
        )
    }

    spell {
        target("target", Targets.Spell)
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Chris Rahn"
        flavorText = "Obsessed with the pursuit of knowledge above all else, vedalken can appear to be cold and emotionless."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2805239-f30a-4eca-a10b-41673daaa287.jpg?1783941736"
    }
}
