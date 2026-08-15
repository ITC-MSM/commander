package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Unexpected Request
 * {2}{R}
 * Sorcery
 * Gain control of target creature until end of turn. Untap that creature. It gains haste until end
 *   of turn. You may attach an Equipment you control to that creature. If you do, unattach it at the
 *   beginning of the next end step.
 *
 * A "threaten" with an optional Equipment rider, composed from existing primitives (no monolithic
 * executor):
 *  - [Effects.GainControl] of the target creature for [Duration.EndOfTurn], then [Effects.Untap]
 *    and a [Duration.EndOfTurn] haste grant — the standard borrow-and-swing package.
 *  - The Equipment to move is an *optional* second target (you may decline by choosing none). A
 *    [ConditionalEffect] gates the attach on an Equipment actually having been chosen
 *    ([Conditions.EntityMatches] resolves to `false` when the optional slot is empty), so the "if
 *    you do" clause is honored — no attach, no scheduled unattach when you decline.
 *  - When an Equipment is chosen, [Effects.AttachTargetEquipmentToCreature] moves it onto the
 *    borrowed creature and a [CreateDelayedTriggerEffect] at [Step.END] unattaches it at the
 *    beginning of the next end step. The Equipment never changes controller, so it remains yours
 *    when the borrowed creature reverts at cleanup.
 */
val UnexpectedRequest = card("Unexpected Request") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Gain control of target creature until end of turn. Untap that creature. It gains " +
        "haste until end of turn. You may attach an Equipment you control to that creature. If you " +
        "do, unattach it at the beginning of the next end step."

    spell {
        // creature = ContextTarget(0), equipment = ContextTarget(1) (declaration order).
        val creature = target("target creature", Targets.Creature)
        val equipment = target(
            "an Equipment you control",
            TargetPermanent(
                filter = TargetFilter(GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT).youControl()),
                optional = true
            )
        )
        effect = Effects.Composite(
            Effects.GainControl(creature, Duration.EndOfTurn),
            Effects.Untap(creature),
            Effects.GrantKeyword(Keyword.HASTE, creature, Duration.EndOfTurn),
            ConditionalEffect(
                // "If you do" — the ConditionEvaluator only dispatches ContextTarget (not the
                // bound-variable handle), so gate on the equipment's positional slot.
                condition = Conditions.EntityMatches(
                    EffectTarget.ContextTarget(1),
                    GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT)
                ),
                effect = Effects.Composite(
                    Effects.AttachTargetEquipmentToCreature(
                        equipmentTarget = equipment,
                        creatureTarget = creature
                    ),
                    CreateDelayedTriggerEffect(
                        step = Step.END,
                        effect = Effects.UnattachEquipment(equipment)
                    )
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "167"
        artist = "Ignatius Budi"
        flavorText = "\"I shall hereby do my best to kidnap you!\""
        imageUri = "https://cards.scryfall.io/normal/front/0/2/0265fd20-a85d-49ce-b338-4c40843a5b18.jpg?1748706387"
    }
}
