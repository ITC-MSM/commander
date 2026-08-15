package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Charming Prince
 * {1}{W}
 * Creature — Human Noble
 * 2/2
 *
 * When this creature enters, choose one —
 * • Scry 2.
 * • You gain 3 life.
 * • Exile another target creature you own. Return it to the battlefield under your control at the
 *   beginning of the next end step.
 *
 * Canonical printing lives here in Throne of Eldraine (earliest real printing); Foundations gets a
 * [com.wingedsheep.sdk.model.Printing] row.
 *
 * The blink mode is the only one that targets, so it is a [Mode.withTarget]; the other two are
 * [Mode.noTarget]. The exiled card is returned by a delayed end-step trigger (SalvationSwan shape).
 * Since the target must be "a creature you own", returning it to the battlefield puts it under its
 * owner's — your — control, matching "under your control".
 */
val CharmingPrince = card("Charming Prince") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Noble"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, choose one —\n" +
        "• Scry 2.\n" +
        "• You gain 3 life.\n" +
        "• Exile another target creature you own. Return it to the battlefield under your control " +
        "at the beginning of the next end step."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ModalEffect(
            modes = listOf(
                Mode.noTarget(Effects.Scry(2), "Scry 2."),
                Mode.noTarget(Effects.GainLife(3), "You gain 3 life."),
                Mode.withTarget(
                    effect = Effects.Move(EffectTarget.ContextTarget(0), Zone.EXILE)
                        .then(
                            CreateDelayedTriggerEffect(
                                step = Step.END,
                                effect = Effects.Move(EffectTarget.ContextTarget(0), Zone.BATTLEFIELD)
                            )
                        ),
                    target = TargetCreature(filter = TargetFilter.Creature.ownedByYou().other()),
                    description = "Exile another target creature you own. Return it to the battlefield " +
                        "under your control at the beginning of the next end step."
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "8"
        artist = "Randy Vargas"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dcb94950-3f3e-4876-84f8-d5e4d9cfecee.jpg?1782707930"
    }
}
