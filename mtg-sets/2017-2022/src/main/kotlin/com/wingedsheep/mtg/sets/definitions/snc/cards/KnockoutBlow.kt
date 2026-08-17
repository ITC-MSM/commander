package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Knockout Blow
 * {2}{W}
 * Instant
 * This spell costs {2} less to cast if it targets a red creature.
 * Knockout Blow deals 4 damage to target attacking or blocking creature and you gain 2 life.
 *
 * The discount is the Ride's End shape — [SpellCostTarget.SelfCast] over
 * [CostReductionSource.FixedIfAnyTargetMatches], a *generic* reduction gated on the spell's own
 * chosen target matching a red-creature filter.
 */
val KnockoutBlow = card("Knockout Blow") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "This spell costs {2} less to cast if it targets a red creature.\nKnockout Blow deals 4 damage to target attacking or blocking creature and you gain 2 life."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature))
        effect = Effects.Composite(
            Effects.DealDamage(4, t),
            Effects.GainLife(2)
        )
    }

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfAnyTargetMatches(
                    amount = 2,
                    filter = GameObjectFilter.Creature.withColor(Color.RED),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "20"
        artist = "Zoltan Boros"
        flavorText = "\"Subsequent efforts to deconstruct this facility will be punished with increasing severity.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b00bbec-61d0-464c-bf82-4ecf5ddb3451.jpg?1783923155"
    }
}
