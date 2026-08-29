package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blood Aspirant
 * {1}{R}
 * Creature — Satyr Berserker
 * 1/1
 *
 * Whenever you sacrifice a permanent, put a +1/+1 counter on this creature.
 * {1}{R}, {T}, Sacrifice a creature or enchantment: This creature deals 1 damage to target creature.
 * That creature can't block this turn.
 *
 * The trigger is [Triggers.YouSacrificeA] — the *per-permanent* template (CR 603.2c), which fires
 * once for each matching permanent rather than once per batch, and whose `ANY` binding lets the
 * Satyr count itself. The two abilities interact by design: the activated ability's sacrifice is
 * paid as a **cost**, and the cost-payment paths emit `PermanentsSacrificedEvent` just like an
 * effect-driven sacrifice does, so activating the second ability grows the Satyr via the first.
 *
 * The activated ability's two clauses are one `Composite` over a single shared target — the damage
 * and the block restriction both read the same bound `target`, so this is a `then` chain and not
 * two requirements.
 */
val BloodAspirant = card("Blood Aspirant") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Satyr Berserker"
    power = 1
    toughness = 1
    oracleText = "Whenever you sacrifice a permanent, put a +1/+1 counter on this creature.\n" +
        "{1}{R}, {T}, Sacrifice a creature or enchantment: This creature deals 1 damage to target creature. " +
        "That creature can't block this turn."

    triggeredAbility {
        trigger = Triggers.YouSacrificeA(GameObjectFilter.Permanent)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{R}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.CreatureOrEnchantment)
        )
        val creature = target("target", Targets.Creature)
        effect = Effects.DealDamage(1, creature) then Effects.CantBlock(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "128"
        artist = "Tyler Walpole"
        flavorText = "It's a small step from revel to bloodbath."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d4f3fa3-ba1f-48dc-a56b-738936f1bf86.jpg"
    }
}
