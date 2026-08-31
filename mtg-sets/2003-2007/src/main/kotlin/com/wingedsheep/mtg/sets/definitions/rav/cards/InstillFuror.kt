package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Instill Furor — Ravnica: City of Guilds #134 (canonical printing, only printing)
 * {1}{R} · Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has "At the beginning of your end step, sacrifice this creature unless it
 * attacked this turn."
 *
 * The printed text grants the ability rather than printing it on the Aura, and that distinction is
 * the whole card, so it is a [GrantTriggeredAbility] over [GroupFilter.attachedCreature] (the Relic
 * Bane shape) rather than a trigger on the Aura itself. Two rulings depend on it:
 *
 * - "Instill Furor grants a triggered ability that triggers at the end of the *creature's
 *   controller's* turn" (2005-10-01). The granted ability is controlled by whoever controls the
 *   creature, so `Triggers.YourEndStep` reads that player's end step — not the Aura controller's.
 *   Stealing the creature moves the clock with it.
 * - "Once the ability triggers, nothing that happens to Instill Furor can prevent the ability from
 *   resolving" (2005-10-01). The ability is an independent object on the stack once it triggers;
 *   nothing here re-reads the Aura at resolution.
 *
 * "Sacrifice … **unless** it attacked this turn" is a resolution-time test, not an intervening "if"
 * (CR 603.4 vs 608.2), so it is a [ConditionalEffect] inside the effect rather than an
 * `interveningIf` on the trigger: the ability always triggers and always goes on the stack, and the
 * attack check happens as it resolves. `Conditions.SourceAttackedThisTurn` reads the *granted*
 * ability's source, which is the enchanted creature, exactly as the printed "it" demands.
 */
val InstillFuror = card("Instill Furor") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"At the beginning of your end step, sacrifice this creature " +
        "unless it attacked this turn.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YourEndStep.event,
                binding = Triggers.YourEndStep.binding,
                effect = ConditionalEffect(
                    condition = Conditions.Not(Conditions.SourceAttackedThisTurn),
                    effect = Effects.SacrificeTarget(EffectTarget.Self),
                ),
                descriptionOverride = "At the beginning of your end step, sacrifice this creature " +
                    "unless it attacked this turn.",
            ),
            filter = GroupFilter.attachedCreature(),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "134"
        artist = "Jim Nelson"
        flavorText = "\"The Rakdos know little of technology, but they definitely know how to push buttons.\"\n—Trivaz, Izzet mage"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6164762-c02d-4553-9064-2b99ff6352c9.jpg?1783943651"
        ruling("2005-10-01", "Instill Furor grants a triggered ability that triggers at the end of the creature's controller's turn.")
        ruling("2005-10-01", "Once the ability triggers, nothing that happens to Instill Furor can prevent the ability from resolving.")
    }
}
