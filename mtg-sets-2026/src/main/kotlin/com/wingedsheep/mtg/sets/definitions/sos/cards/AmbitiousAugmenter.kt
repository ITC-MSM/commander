package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.increment
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ambitious Augmenter
 * {G}
 * Creature — Turtle Wizard
 * 1/1
 *
 * Increment (Whenever you cast a spell, if the amount of mana you spent is greater than this
 * creature's power or toughness, put a +1/+1 counter on this creature.)
 * When this creature dies, if it had one or more counters on it, create a 0/0 green and blue
 * Fractal creature token, then put this creature's counters on that token.
 *
 * `increment()` is the SOS ability-word keyword (no real keyword added). The dies trigger uses
 * the intervening-if `Conditions.TriggeringEntityHadCounters` (CR 603.4 — reads the dying
 * creature's last-known total counter count). The effect composes two atomic facades: create the
 * 0/0 Fractal (published to the `CREATED_TOKENS` pipeline collection), then move *every* last-known
 * counter kind from the dying creature onto that just-created token via
 * `EffectTarget.PipelineTarget(CREATED_TOKENS, 0)`. Because the creature only ever bears +1/+1
 * counters from Increment, moving all last-known counters faithfully reproduces the printed wording.
 */
val AmbitiousAugmenter = card("Ambitious Augmenter") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Turtle Wizard"
    power = 1
    toughness = 1
    oracleText = "Increment (Whenever you cast a spell, if the amount of mana you spent is greater " +
        "than this creature's power or toughness, put a +1/+1 counter on this creature.)\n" +
        "When this creature dies, if it had one or more counters on it, create a 0/0 green and blue " +
        "Fractal creature token, then put this creature's counters on that token."

    increment()

    triggeredAbility {
        trigger = Triggers.Dies
        triggerCondition = Conditions.TriggeringEntityHadCounters
        effect = Effects.CreateToken(
            power = 0,
            toughness = 0,
            colors = setOf(Color.GREEN, Color.BLUE),
            creatureTypes = setOf("Fractal"),
            imageUri = "https://cards.scryfall.io/normal/front/d/e/de564776-9d88-4533-8717-842eecdd0594.jpg?1775828279"
        ).then(
            Effects.MoveAllLastKnownCounters(EffectTarget.PipelineTarget(CREATED_TOKENS, 0))
        )
        description = "When this creature dies, if it had one or more counters on it, create a 0/0 " +
            "green and blue Fractal creature token, then put this creature's counters on that token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "140"
        artist = "Mariah Tekulve"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85629088-2007-4db5-9397-bac12a3d7498.jpg?1775937950"
    }
}
