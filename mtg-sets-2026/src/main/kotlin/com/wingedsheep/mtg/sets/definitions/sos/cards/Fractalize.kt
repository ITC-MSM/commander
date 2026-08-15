package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Fractalize
 * {X}{U}
 * Instant
 *
 * Until end of turn, target creature becomes a green and blue Fractal with base power and toughness
 * each equal to X plus 1. (It loses all other colors and creature types.)
 *
 * One `BecomeCreature` (animate) transform on the target creature, until end of turn:
 *  - **base P/T** = `DynamicAmount.Add(XValue, Fixed(1))` — X plus 1, fed through the now-dynamic
 *    `BecomeCreatureEffect` power/toughness (evaluated once at resolution, CR 613.4c).
 *  - **creature types** set to exactly `{Fractal}` (Layer 4 set effect → loses all other creature
 *    types).
 *  - **colors** set to exactly green + blue (Layer 5 set effect → loses all other colors).
 *  - **image** overridden (display-only) with the Fractal token's art for the duration, so the
 *    animated creature visibly reads as a Fractal; reverts at end of turn with the rest of the
 *    animate. The card's own `metadata.imageUri` still points at the real Fractalize printing.
 * No keywords are granted. Cast with X = 0 it becomes a 1/1 Fractal.
 */
val Fractalize = card("Fractalize") {
    manaCost = "{X}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Until end of turn, target creature becomes a green and blue Fractal with base " +
        "power and toughness each equal to X plus 1. (It loses all other colors and creature types.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.BecomeCreature(
            target = creature,
            power = DynamicAmount.Add(DynamicAmount.XValue, DynamicAmount.Fixed(1)),
            toughness = DynamicAmount.Add(DynamicAmount.XValue, DynamicAmount.Fixed(1)),
            creatureTypes = setOf("Fractal"),
            colors = setOf(Color.GREEN.name, Color.BLUE.name),
            imageUri = "https://cards.scryfall.io/normal/front/8/b/8b5f1fdb-04df-4224-acb4-7819c37565f5.jpg?1775828306",
            duration = Duration.EndOfTurn,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "51"
        artist = "Andrew Mar"
        flavorText = "\"Return your lizards to their natural size before class ends. We don't want " +
            "another incident.\"\n—Professor Berta"
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3c3b19b-01b6-4f5a-b428-513b778c5d89.jpg?1775937264"
    }
}
