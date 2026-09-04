package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Nettlevine Blight
 * {4}{B}{B}
 * Enchantment — Aura
 * Enchant creature or land
 * Enchanted permanent has "At the beginning of your end step, sacrifice this permanent and attach
 * Nettlevine Blight to a creature or land you control."
 *
 * The whole card turns on *who* the quoted "you" is, and the 2007-10-01 ruling spells it out: the
 * ability is granted to the enchanted permanent, so it triggers on that permanent's controller's
 * end step, that player sacrifices, and that player picks the new host from permanents *they*
 * control. Printing the ability on the Aura instead would hand every one of those to the Aura's
 * controller — precisely backwards for a card you play on an opponent's permanent, and it would
 * walk the Blight through your own board instead of eating theirs. [GrantTriggeredAbility] over the
 * default `Scope.AttachedTo` filter is what makes it right: the engine indexes a granted trigger on
 * the permanent it was granted to, so [Triggers.YourEndStep]'s `Player.You` and the pipeline's
 * [CardSource.ControlledPermanents] both read that permanent's controller. Inevitable End is the
 * same shape one step smaller.
 *
 * Inside the granted ability the two nouns are *different objects*, and each has its own handle:
 *
 * - **"this permanent"** is [EffectTarget.Self] — in a granted ability, Self is the host that
 *   received the ability, not the granter.
 * - **"Nettlevine Blight"** is [EffectTarget.GrantingSource] — the Aura whose static granted this
 *   ability, captured when the trigger went on the stack. Using `Self` for both would sacrifice the
 *   host and then attach the host to something.
 *
 * [Effects.AttachTargetEquipmentToCreature] is named for the Equipment case it was written for, but
 * its executor reads neither type: it moves an `AttachedToComponent` from one permanent to another.
 * An Aura moving onto a land is exactly what it does.
 *
 * The re-attach is a resolution-time *choice*, not a target (the printed line has no "target"), so
 * it is a gather over the permanents that player controls followed by `chooseExactly(1)`. Gathering
 * *after* the sacrifice is what makes the just-sacrificed host ineligible without an explicit
 * exclusion, and `ChooseExactly`'s clamp handles the empty board: with nothing left to enchant the
 * selection is empty, `ifNotEmpty` skips the attach, and the now-hostless Aura is put into its
 * owner's graveyard by the unattached-Auras state-based action (CR 704.5m) — which runs after the
 * resolution, never inside it, so the attach above still lands while the Aura is briefly host-less.
 * That mid-resolution window is the one Breath of Fury opened (#2203).
 */
val NettlevineBlight = card("Nettlevine Blight") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature or land\n" +
        "Enchanted permanent has \"At the beginning of your end step, sacrifice this permanent " +
        "and attach Nettlevine Blight to a creature or land you control.\""

    auraTarget = TargetPermanent(filter = TargetFilter.CreatureOrLandPermanent)

    staticAbility {
        ability = GrantTriggeredAbility(
            ability = TriggeredAbility.create(
                trigger = Triggers.YourEndStep.event,
                binding = Triggers.YourEndStep.binding,
                effect = Effects.Pipeline {
                    run(Effects.SacrificeTarget(EffectTarget.Self))
                    val hosts = gather(
                        CardSource.ControlledPermanents(filter = GameObjectFilter.CreatureOrLand),
                        name = "newHosts"
                    )
                    val newHost = chooseExactly(
                        1,
                        from = hosts,
                        prompt = "Choose a creature or land to attach Nettlevine Blight to",
                        useTargetingUI = true
                    )
                    ifNotEmpty(newHost) {
                        run(
                            Effects.AttachTargetEquipmentToCreature(
                                equipmentTarget = EffectTarget.GrantingSource,
                                creatureTarget = EffectTarget.PipelineTarget(newHost.key)
                            )
                        )
                    }
                },
                descriptionOverride = "At the beginning of your end step, sacrifice this permanent " +
                    "and attach Nettlevine Blight to a creature or land you control."
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "131"
        artist = "Michael Sutfin"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/dd6b1240-0bc3-401a-9cc3-3acaea871a3d.jpg?1783942885"
        ruling(
            "2007-10-01",
            "Nettlevine Blight grants the triggered ability to the enchanted permanent, so \"you\" " +
                "refers to that permanent's controller. The ability will trigger at the end of " +
                "that player's turn, and that player chooses the new creature or land to attach " +
                "Nettlevine Blight to. The player must choose a creature or land they control."
        )
    }
}
