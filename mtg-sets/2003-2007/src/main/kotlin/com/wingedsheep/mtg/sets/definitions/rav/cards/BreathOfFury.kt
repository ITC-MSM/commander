package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Breath of Fury
 * {2}{R}{R}
 * Enchantment — Aura
 * Enchant creature you control
 * When enchanted creature deals combat damage to a player, sacrifice it and attach this Aura to a
 * creature you control. If you do, untap all creatures you control and after this phase, there is
 * an additional combat phase.
 *
 * The re-attach is a resolution-time *choice*, not a target — so it is a gather over the creatures
 * you control followed by a `chooseExactly(1)`, not a `target()` handle. Gathering *after* the
 * sacrifice is what makes the sacrificed host ineligible without an explicit exclusion, and
 * `ChooseExactly`'s clamp is the 2005-10-01 ruling: with no legal creature left the selection is
 * empty, `ifNotEmpty` skips the payoff, and the now-unattached Aura is put into its owner's
 * graveyard by the unattached-Auras state-based action (never mid-resolution, so the attach above
 * still lands while the Aura is host-less).
 */
val BreathOfFury = card("Breath of Fury") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature you control\n" +
        "When enchanted creature deals combat damage to a player, sacrifice it and attach this " +
        "Aura to a creature you control. If you do, untap all creatures you control and after " +
        "this phase, there is an additional combat phase."

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            binding = TriggerBinding.ATTACHED,
        )
        effect = Effects.Pipeline {
            run(Effects.SacrificeTarget(EffectTarget.EnchantedCreature))
            val hosts = gather(CardSource.ControlledPermanents(filter = GameObjectFilter.Creature))
            val newHost = chooseExactly(
                1,
                from = hosts,
                prompt = "Choose a creature to attach Breath of Fury to",
                useTargetingUI = true,
            )
            ifNotEmpty(newHost) {
                run(Effects.AttachEquipment(EffectTarget.PipelineTarget(newHost.key)))
                run(Patterns.Group.untapGroup(GroupFilter.AllCreaturesYouControl))
                run(Effects.AddCombatPhase)
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "116"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/dbef6f4a-f9a0-4a4c-b8a2-6c3a8fb7e14a.jpg?1783943657"
        ruling(
            "2005-10-01",
            "If there isn't a legal creature to attach Breath of Fury to after the enchanted " +
                "creature is sacrificed, you don't untap your creatures or get an additional " +
                "combat phase, and Breath of Fury is put into its owner's graveyard as a " +
                "state-based action."
        )
    }
}
