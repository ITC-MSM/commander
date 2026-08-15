package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Momo, Friendly Flier
 * {W}
 * Legendary Creature — Lemur Bat Ally
 * 1/1
 *
 * Flying
 * The first non-Lemur creature spell with flying you cast during each of your turns
 * costs {1} less to cast.
 * Whenever another creature you control with flying enters, Momo gets +1/+1 until end of turn.
 */
val MomoFriendlyFlier = card("Momo, Friendly Flier") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Lemur Bat Ally"
    oracleText = "Flying\n" +
        "The first non-Lemur creature spell with flying you cast during each of your turns costs {1} less to cast.\n" +
        "Whenever another creature you control with flying enters, Momo gets +1/+1 until end of turn."
    power = 1
    toughness = 1

    keywords(Keyword.FLYING)

    // Non-Lemur creature spell with flying.
    val flyingCreature = GameObjectFilter.Creature
        .withKeyword(Keyword.FLYING)
        .notSubtype(Subtype("Lemur"))

    // The first such spell you cast during each of *your* turns costs {1} less. Gated on it being
    // your turn AND no matching spell having been cast yet this turn (the spell currently being
    // cast is not yet recorded at cost-calculation time, so "none cast yet" == "this is the first").
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(flyingCreature),
            modification = CostModification.ReduceGeneric(1),
            gating = CostGating.OnlyIf(
                Conditions.All(
                    Conditions.IsYourTurn,
                    Conditions.Not(Conditions.YouCastSpellsThisTurn(atLeast = 1, filter = flyingCreature)),
                )
            ),
        )
    }

    // Whenever another creature you control with flying enters, Momo gets +1/+1 until end of turn.
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl().withKeyword(Keyword.FLYING),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "29"
        artist = "Brandon L. Hunt"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c472ef84-a632-4ad7-853c-60588a7a4b12.jpg?1764120082"
    }
}
