package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Subtype
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
 * Squash
 * {4}{R}
 * Instant
 * This spell costs {3} less to cast if you control a Giant.
 * Squash deals 6 damage to target creature or planeswalker.
 *
 * The cost reduction is a [ModifySpellCost] static on the spell itself ([SpellCostTarget.SelfCast]),
 * gated on controlling a Giant. It rides the existing spell-cost rail rather than a second one, so
 * the discount is visible in the cost preview the client shows before the spell is cast.
 */
val Squash = card("Squash") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "This spell costs {3} less to cast if you control a Giant.\n" +
        "Squash deals 6 damage to target creature or planeswalker."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(3),
            gating = CostGating.OnlyIf(
                Conditions.YouControl(GameObjectFilter.Permanent.withSubtype(Subtype.GIANT))
            )
        )
    }

    spell {
        val victim = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(6, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Caio Monteiro"
        flavorText = "The troll had recently crushed a human in much the same way. Sadly, he expired too quickly to appreciate the irony."
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0b99299-bf84-4654-a331-e4406768b33c.jpg"
    }
}
