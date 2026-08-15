package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * The Fire Crystal
 * {2}{R}{R}
 * Legendary Artifact
 *
 * Red spells you cast cost {1} less to cast.
 * Creatures you control have haste.
 * {4}{R}{R}, {T}: Create a token that's a copy of target creature you control.
 *   Sacrifice it at the beginning of the next end step.
 *
 * Notes:
 *  - The cost reduction reduces only generic mana in the total cost of red spells you cast
 *    (Scryfall ruling 2025-06-06); modeled with [CostModification.ReduceGeneric].
 *  - The token copy + "sacrifice at the beginning of the next end step" is the
 *    [Effects.CreateTokenCopyOfTarget] effect with `sacrificeAtStep = Step.END`, which creates
 *    the delayed sacrifice trigger automatically (the next end step, not gated to yours).
 */
val TheFireCrystal = card("The Fire Crystal") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Artifact"
    oracleText = "Red spells you cast cost {1} less to cast.\n" +
        "Creatures you control have haste.\n" +
        "{4}{R}{R}, {T}: Create a token that's a copy of target creature you control. " +
        "Sacrifice it at the beginning of the next end step."

    // Red spells you cast cost {1} less to cast.
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withColor(Color.RED)),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    // Creatures you control have haste.
    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.HASTE,
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
        )
    }

    // {4}{R}{R}, {T}: Create a token that's a copy of target creature you control.
    // Sacrifice it at the beginning of the next end step.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}{R}{R}"), Costs.Tap)
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.CreateTokenCopyOfTarget(
            target = creature,
            sacrificeAtStep = Step.END,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "135"
        artist = "Pablo Mendoza"
        flavorText = "\"Warriors of Light...Only you can restore hope to the world, for yours is " +
            "the light that can balance out the darkness...\""
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea430b17-2014-4b8e-b53f-43bcfc06f7cd.jpg?1748706272"
    }
}
