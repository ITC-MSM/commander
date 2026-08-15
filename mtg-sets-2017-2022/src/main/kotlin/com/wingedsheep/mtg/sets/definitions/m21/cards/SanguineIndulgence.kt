package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sanguine Indulgence
 * {3}{B}
 * Sorcery
 * This spell costs {3} less to cast if you've gained 3 or more life this turn.
 * Return up to two target creature cards from your graveyard to your hand.
 *
 * The discount is a self-cast [ModifySpellCost] gated by [CostGating.OnlyIf] on
 * [Conditions.YouGainedLifeThisTurnAtLeast] (3), backed by the `LIFE_GAINED` turn tracker —
 * so it is evaluated as the spell is cast (CR 601.2f) and drops the whole generic component,
 * leaving {B}.
 *
 * "Up to two target" is `TargetObject(count = 2, optional = true)`, so zero, one, or two cards
 * may be chosen; each chosen card is returned independently via [ForEachTargetEffect] (a card
 * that left the graveyard in response simply isn't returned, and the spell still resolves for
 * the remaining legal target).
 */
val SanguineIndulgence = card("Sanguine Indulgence") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "This spell costs {3} less to cast if you've gained 3 or more life this turn.\n" +
        "Return up to two target creature cards from your graveyard to your hand."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(3),
            gating = CostGating.OnlyIf(Conditions.YouGainedLifeThisTurnAtLeast(3)),
        )
    }

    spell {
        target = TargetObject(
            count = 2,
            optional = true,
            filter = TargetFilter.CreatureInYourGraveyard,
        )
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.HAND)),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "121"
        artist = "Andrey Kuzinskiy"
        flavorText = "\"Drink from the vein? How provincial.\"\n—Lord Duchorvin"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/abfcd08a-cfb5-4d34-b950-f57a88c5cb8e.jpg?1783930700"
    }
}
