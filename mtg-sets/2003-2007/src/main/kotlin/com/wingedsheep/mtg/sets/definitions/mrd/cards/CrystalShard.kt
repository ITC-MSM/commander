package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Crystal Shard — Mirrodin #159 (canonical printing)
 * {3} · Artifact
 *
 * {3}, {T} or {U}, {T}: Return target creature to its owner's hand unless its controller pays {1}.
 *
 * The blue member of the Mirrodin Shard cycle. The dual cost is modelled the same way as
 * [GraniteShard] and [PearlShard]: one printed ability with two alternative cost sets becomes two
 * `activatedAbility` blocks with identical effects (2004-10-04 ruling: pay one or the other, never
 * both), competing for the shard's single `{T}`.
 *
 * "Unless its controller pays {1}" is an *inverted* gate — the bounce is the `otherwise` branch, so
 * paying is what stops it. Both halves of the gate point at the targeted creature's controller
 * rather than the shard's: `decisionMaker = TargetController` prompts them, and
 * `PayDynamicMana(payer = ControllerOf(...))` charges them. `Gate.MayPay` skips the prompt entirely
 * when the cost is unpayable and goes straight to the bounce, so a tapped-out opponent is never
 * offered an impossible "yes". The `then` branch is deliberately empty — a paid {1} simply keeps the
 * creature where it is.
 *
 * `colorIdentity = "U"` because the {U} alternative puts blue in the card's identity even though the
 * card itself is colorless (CR 903.4).
 */
val CrystalShard = card("Crystal Shard") {
    manaCost = "{3}"
    colorIdentity = "U"
    typeLine = "Artifact"
    oracleText = "{3}, {T} or {U}, {T}: Return target creature to its owner's hand unless its controller pays {1}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val creature = target("creature", Targets.Creature)
        effect = bounceUnlessControllerPays(creature)
        description = "{3}, {T}: Return target creature to its owner's hand unless its controller pays {1}."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        val creature = target("creature", Targets.Creature)
        effect = bounceUnlessControllerPays(creature)
        description = "{U}, {T}: Return target creature to its owner's hand unless its controller pays {1}."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "159"
        artist = "Doug Chaffee"
        flavorText = "The vedalken know it is not of this world, so they know that this world is not the only one."
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1c1d05b-92be-40d7-859f-75293a531a84.jpg?1783944524"
        ruling("2004-10-04", "You can pay either of the two costs (but not both at the same time) to activate the ability.")
    }
}

private fun bounceUnlessControllerPays(creature: EffectTarget) = GatedEffect(
    gate = Gate.MayPay(
        Effects.PayDynamicMana(
            amount = DynamicAmount.Fixed(1),
            payer = Player.ControllerOf("target creature")
        )
    ),
    decisionMaker = EffectTarget.TargetController,
    then = Effects.Composite(emptyList()),
    otherwise = Effects.ReturnToHand(creature),
    descriptionOverride = "Return target creature to its owner's hand unless its controller pays {1}."
)
