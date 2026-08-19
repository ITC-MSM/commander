package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostZone
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.SelfAlternativeCost
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Force of Vigor
 * {2}{G}{G}
 * Instant
 *
 * If it's not your turn, you may exile a green card from your hand rather than pay this spell's
 * mana cost.
 * Destroy up to two target artifacts and/or enchantments.
 *
 * The signature "Force" cycle alternative cost (Modern Horizons): a free [SelfAlternativeCost] —
 * no mana, one non-mana additional cost (exile a green card from hand) — gated by
 * [Conditions.IsNotYourTurn], mirroring Zahid, Djinn of the Lamp's tap-an-artifact additional cost
 * and Blasphemous Edict's condition-gated free alternative. The destroy effect follows Rack and
 * Ruin's "two target artifacts" shape (`ForEachTargetEffect` over a `count = 2` target, applying
 * `Effects.Destroy` per resolved target via `ContextTarget(0)`), widened to `optional = true` for
 * "up to two" and to an artifact-or-enchantment filter.
 */
val ForceOfVigor = card("Force of Vigor") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "If it's not your turn, you may exile a green card from your hand rather than " +
        "pay this spell's mana cost.\nDestroy up to two target artifacts and/or enchantments."

    selfAlternativeCost = SelfAlternativeCost(
        manaCost = ManaCost.parse("{0}"),
        additionalCosts = listOf(
            Costs.additional.ExileCards(
                count = 1,
                filter = GameObjectFilter.Any.withColor(Color.GREEN),
                fromZone = CostZone.HAND
            )
        ),
        condition = Conditions.IsNotYourTurn
    )

    spell {
        target(
            "targets",
            TargetPermanent(
                count = 2,
                optional = true,
                filter = TargetFilter(GameObjectFilter.Artifact or GameObjectFilter.Enchantment)
            )
        )
        effect = ForEachTargetEffect(
            listOf(Effects.Destroy(EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "164"
        artist = "Randy Vargas"
        flavorText = "The vines overgrew the construct, snapping gears and soaking up aether."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/017c415b-d635-43c6-92b8-8c95d1c4ff8d.jpg?1783933099"
    }
}
