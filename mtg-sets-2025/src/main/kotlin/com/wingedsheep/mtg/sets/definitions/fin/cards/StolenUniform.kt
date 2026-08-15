package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerExpiry
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Stolen Uniform
 * {U}
 * Instant
 *
 * Choose target creature you control and target Equipment. Gain control of that Equipment until
 * end of turn. Attach it to the chosen creature. When you lose control of that Equipment this turn,
 * if it's attached to a creature you control, unattach it.
 *
 * Composition (no monolithic executor):
 *  - [Effects.GainControl] of the targeted Equipment, [Duration.EndOfTurn].
 *  - [Effects.AttachTargetEquipmentToCreature] force-attaches it to the chosen creature. Two
 *    independent targets: if the creature target is illegal at resolution, the attach is a no-op but
 *    you still gain control of the Equipment (per the card's ruling).
 *  - A reflexive "when you lose control of that Equipment" delayed trigger
 *    ([Triggers.LoseControlOfWatched] = `ControlChangeEvent(LOST)`) scoped to the Equipment. It
 *    fires on any mid-turn control change away from you and, if the Equipment is still attached to a
 *    creature you control, unattaches it ([Effects.UnattachEquipment]).
 */
val StolenUniform = card("Stolen Uniform") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose target creature you control and target Equipment. Gain control of that " +
        "Equipment until end of turn. Attach it to the chosen creature. When you lose control of " +
        "that Equipment this turn, if it's attached to a creature you control, unattach it."

    spell {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        val equipment = target(
            "target Equipment",
            TargetPermanent(
                filter = TargetFilter(GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT))
            )
        )
        effect = Effects.Composite(
            Effects.GainControl(equipment, Duration.EndOfTurn),
            Effects.AttachTargetEquipmentToCreature(
                equipmentTarget = equipment,
                creatureTarget = creature
            ),
            CreateDelayedTriggerEffect(
                trigger = Triggers.LoseControlOfWatched,
                watchedTarget = equipment,
                fireOnce = true,
                expiry = DelayedTriggerExpiry.EndOfTurn,
                effect = ConditionalEffect(
                    condition = Conditions.EntityMatches(
                        EffectTarget.TriggeringEntity,
                        GameObjectFilter.Any.attachedTo(GameObjectFilter.Creature.youControl())
                    ),
                    effect = Effects.UnattachEquipment(EffectTarget.TriggeringEntity)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "75"
        artist = "Chris Rallis"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d80c511-2f4d-4f77-8143-7b49b2b19fae.jpg"
    }
}
