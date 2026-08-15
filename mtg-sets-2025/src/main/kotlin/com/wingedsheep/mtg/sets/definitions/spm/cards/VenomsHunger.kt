package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Venom's Hunger
 * {4}{B}
 * Sorcery
 * This spell costs {2} less to cast if you control a Villain.
 * Destroy target creature. You gain 2 life.
 */
val VenomsHunger = card("Venom's Hunger") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "This spell costs {2} less to cast if you control a Villain.\nDestroy target creature. You gain 2 life."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfControlFilter(
                    amount = 2,
                    filter = GameObjectFilter.Creature.withSubtype("Villain")
                )
            )
        )
    }

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Composite(
            Effects.Move(t, Zone.GRAVEYARD, byDestruction = true),
            GainLifeEffect(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Dave DeVries"
        flavorText = "Sinner takes all."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/01d276cd-e4ad-488f-8447-004aefad1ebb.jpg?1757377218"
    }
}
