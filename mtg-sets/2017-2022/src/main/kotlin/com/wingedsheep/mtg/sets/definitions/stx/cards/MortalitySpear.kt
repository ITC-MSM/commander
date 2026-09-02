package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Mortality Spear — Strixhaven: School of Mages #207 (canonical printing)
 * {2}{B}{G} · Instant
 *
 * This spell costs {2} less to cast if you gained life this turn.
 * Destroy target nonland permanent.
 *
 * The discount is a [ModifySpellCost] static on the spell itself ([SpellCostTarget.SelfCast]),
 * gated on the per-turn life-gained tracker via [Conditions.YouGainedLifeThisTurn], so it rides
 * the existing spell-cost rail and shows in the client's cost preview. The body is a plain
 * [Effects.Destroy] of a nonland permanent.
 */
val MortalitySpear = card("Mortality Spear") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Instant"
    oracleText =
        "This spell costs {2} less to cast if you gained life this turn.\n" +
        "Destroy target nonland permanent."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(2),
            gating = CostGating.OnlyIf(Conditions.YouGainedLifeThisTurn)
        )
    }

    spell {
        val victim = target("target nonland permanent", Targets.NonlandPermanent)
        effect = Effects.Destroy(victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "207"
        artist = "PINDURSKI"
        flavorText = "\"Death is my life's work.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1f39fe7-dc12-49c9-80ac-4135dc1f8f08.jpg?1783927305"
    }
}
