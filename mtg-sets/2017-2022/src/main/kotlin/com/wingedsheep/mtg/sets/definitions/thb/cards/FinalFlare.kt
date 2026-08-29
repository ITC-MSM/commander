package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Final Flare
 * {2}{R}
 * Instant
 *
 * As an additional cost to cast this spell, sacrifice a creature or enchantment.
 * Final Flare deals 5 damage to target creature.
 *
 * The sacrifice is an [Costs.additional] atom, not a target: it is chosen and paid while the spell
 * is being cast, so nothing can respond between the sacrifice and the spell going on the stack, and
 * the sacrificed permanent is never a legal object for the damage. [GameObjectFilter.CreatureOrEnchantment]
 * is the printed noun phrase — a creature *or* an enchantment, so an enchantment creature qualifies
 * on either half.
 */
val FinalFlare = card("Final Flare") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature or enchantment.\n" +
        "Final Flare deals 5 damage to target creature."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.CreatureOrEnchantment))

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(5, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "134"
        artist = "Kev Walker"
        flavorText = "Those who fought without honor in life are taken to Agonas and doomed to " +
            "fight forever in its arenas."
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0c95dee-a480-4878-a967-9e46be9ee372.jpg"
    }
}
