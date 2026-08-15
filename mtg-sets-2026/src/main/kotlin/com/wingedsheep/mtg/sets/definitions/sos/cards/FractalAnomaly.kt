package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.TurnTracker

/**
 * Fractal Anomaly
 * {U}
 * Instant
 *
 * Create a 0/0 green and blue Fractal creature token and put X +1/+1 counters on it, where X is
 * the number of cards you've drawn this turn.
 *
 * The Fractal is created (published to the [CREATED_TOKENS] pipeline collection), then X +1/+1
 * counters land on that just-created token via `PipelineTarget(CREATED_TOKENS, 0)` (same shape as
 * Wild Hypothesis / Fractal Tender). Here X is `TurnTracking(You, CARDS_DRAWN)` — the number of
 * cards the controller has drawn this turn (CR 120), backed by `CardsDrawnThisTurnComponent`.
 */
val FractalAnomaly = card("Fractal Anomaly") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Create a 0/0 green and blue Fractal creature token and put X +1/+1 counters on " +
        "it, where X is the number of cards you've drawn this turn."

    spell {
        effect = Effects.CreateToken(
            power = 0,
            toughness = 0,
            colors = setOf(Color.GREEN, Color.BLUE),
            creatureTypes = setOf("Fractal"),
            imageUri = "https://cards.scryfall.io/normal/front/d/e/de564776-9d88-4533-8717-842eecdd0594.jpg?1775828279"
        )
            .then(
                Effects.AddDynamicCounters(
                    Counters.PLUS_ONE_PLUS_ONE,
                    DynamicAmount.TurnTracking(Player.You, TurnTracker.CARDS_DRAWN),
                    EffectTarget.PipelineTarget(CREATED_TOKENS, 0)
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "Steve Ellis"
        flavorText = "\"Dear dad, please send sixteen tons of krill in your next care package. " +
            "Will explain at semester break.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1975a61-aef0-49a6-a6d6-c3a37e2e2b22.jpg?1775937257"
    }
}
