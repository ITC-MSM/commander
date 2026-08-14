package com.wingedsheep.mtg.sets.definitions.ddq.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Eerie Interlude
 * {2}{W}
 * Instant
 * Exile any number of target creatures you control. Return those cards to the battlefield under
 * their owner's control at the beginning of the next end step.
 *
 * Canonical printing is DDQ (pre-SOI). Same multi-blink shape as Morningtide's Light.
 */
val EerieInterlude = card("Eerie Interlude") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText =
        "Exile any number of target creatures you control. Return those cards to the battlefield " +
            "under their owner's control at the beginning of the next end step."

    spell {
        target(
            "any number of target creatures you control",
            TargetCreature(
                unlimited = true,
                optional = true,
                filter = TargetFilter.Creature.youControl(),
            ),
        )
        effect = ForEachTargetEffect(
            effects = listOf(
                Effects.Move(EffectTarget.ContextTarget(0), Zone.EXILE),
                CreateDelayedTriggerEffect(
                    step = Step.END,
                    effect = Effects.Move(
                        target = EffectTarget.ContextTarget(0),
                        destination = Zone.BATTLEFIELD,
                    ),
                ),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "8"
        artist = "Svetlin Velinov"
        imageUri =
            "https://cards.scryfall.io/normal/front/a/a/aa8e63c6-6bbc-405d-84c0-6958931ad310.jpg?1783937858"
    }
}
