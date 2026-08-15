package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Snarl Song
 * {5}{G}
 * Sorcery
 *
 * Converge — Create two 0/0 green and blue Fractal creature tokens. Put X +1/+1 counters on each
 * of them and you gain X life, where X is the number of colors of mana spent to cast this spell.
 *
 * Converge: X = [DynamicAmounts.colorsOfManaSpent] (`DynamicAmount.DistinctColorsManaSpent`),
 * resolved while the spell is still on the stack so it reads the live per-colour payment buckets.
 * Both Fractals are created with one `CreateToken(count = 2)` (publishing both entity IDs to the
 * [CREATED_TOKENS] pipeline collection at indices 0 and 1); X +1/+1 counters then land on each via
 * `PipelineTarget(CREATED_TOKENS, index)` — the same per-created-token addressing as Wild Hypothesis
 * / Fractal Tender. Finally the controller gains X life.
 */
val SnarlSong = card("Snarl Song") {
    manaCost = "{5}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Converge — Create two 0/0 green and blue Fractal creature tokens. Put X +1/+1 " +
        "counters on each of them and you gain X life, where X is the number of colors of mana " +
        "spent to cast this spell."

    spell {
        effect = Effects.CreateToken(
            power = 0,
            toughness = 0,
            colors = setOf(Color.GREEN, Color.BLUE),
            creatureTypes = setOf("Fractal"),
            count = 2,
            imageUri = "https://cards.scryfall.io/normal/front/d/e/de564776-9d88-4533-8717-842eecdd0594.jpg?1775828279"
        )
            .then(
                Effects.AddDynamicCounters(
                    Counters.PLUS_ONE_PLUS_ONE,
                    DynamicAmounts.colorsOfManaSpent(),
                    EffectTarget.PipelineTarget(CREATED_TOKENS, 0)
                )
            )
            .then(
                Effects.AddDynamicCounters(
                    Counters.PLUS_ONE_PLUS_ONE,
                    DynamicAmounts.colorsOfManaSpent(),
                    EffectTarget.PipelineTarget(CREATED_TOKENS, 1)
                )
            )
            .then(Effects.GainLife(DynamicAmounts.colorsOfManaSpent()))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "161"
        artist = "Josu Hernaiz"
        flavorText = "\"Archaics and mana snarls have both existed since the beginning of time. Coincidence? Highly unlikely.\"\n—Zimone"
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fc4c7fa2-aebb-4636-9afd-f1010c923316.jpg?1775938101"
    }
}
