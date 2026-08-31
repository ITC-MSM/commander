package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fodder Launch
 * {3}{B}
 * Kindred Sorcery — Goblin
 *
 * As an additional cost to cast this spell, sacrifice a Goblin.
 * Target creature gets -5/-5 until end of turn. Fodder Launch deals 5 damage to that creature's
 * controller.
 *
 * The Goblin is sacrificed on announcement (an additional cost, so it happens whether or not the
 * spell resolves), which is also what makes this the sacrifice outlet half of the Boggart deck.
 *
 * "That creature's controller" is [EffectTarget.TargetController] — read from the *target* rather
 * than from the spell's controller, so the damage follows a creature that changed hands.
 *
 * Note: "Tribal" was errata'd to "Kindred" in 2024.
 */
val FodderLaunch = card("Fodder Launch") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Kindred Sorcery — Goblin"
    oracleText = "As an additional cost to cast this spell, sacrifice a Goblin.\n" +
        "Target creature gets -5/-5 until end of turn. Fodder Launch deals 5 damage to that " +
        "creature's controller."

    additionalCost(
        Costs.additional.SacrificePermanent(
            GameObjectFilter.Permanent.withSubtype(Subtype.GOBLIN)
        )
    )

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-5, -5, creature) then
            Effects.DealDamage(5, EffectTarget.TargetController)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "114"
        artist = "Nils Hamm"
        flavorText = "Leave it to a boggart to come up with a projectile as disgusting as it is deadly."
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d000670f-1151-4abf-a7ec-b35a6e587183.jpg?1783942890"
    }
}
