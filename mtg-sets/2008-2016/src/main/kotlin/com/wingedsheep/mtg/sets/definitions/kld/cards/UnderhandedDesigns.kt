package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Underhanded Designs
 * {1}{B}
 * Enchantment
 *
 * Whenever an artifact you control enters, you may pay {1}. If you do, each opponent loses 1 life
 * and you gain 1 life.
 * {1}{B}, Sacrifice this enchantment: Destroy target creature. Activate only if you control two or
 * more artifacts.
 *
 * The trigger takes `TriggerBinding.ANY` — an enchantment can never be the artifact that entered,
 * so "an artifact you control" is the whole group rather than "another". "You may pay {1}. If you
 * do, ..." is [MayPayManaEffect] (a `Gate.MayPay` over the payment), not a reflexive trigger.
 */
val UnderhandedDesigns = card("Underhanded Designs") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "Whenever an artifact you control enters, you may pay {1}. If you do, each opponent loses 1 life and you gain 1 life.\n" +
        "{1}{B}, Sacrifice this enchantment: Destroy target creature. Activate only if you control two or more artifacts."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Artifact.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = MayPayManaEffect(
            ManaCost.parse("{1}"),
            Effects.Composite(
                Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
                Effects.GainLife(1),
            ),
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.SacrificeSelf)
        val t = target("target", Targets.Creature)
        effect = Effects.Destroy(t)
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.YouControlAtLeast(2, GameObjectFilter.Artifact)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "104"
        artist = "Anastasia Ovchinnikova"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/4392fe0a-a15e-46c6-9a3d-8e30e4dab17f.jpg?1783937200"
    }
}
