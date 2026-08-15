package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Devout Decree
 * {1}{W}
 * Sorcery
 *
 * Exile target creature or planeswalker that's black or red. Scry 1.
 *
 * The target is a single creature-or-planeswalker restricted to black or red via
 * [GameObjectFilter.CreatureOrPlaneswalker] + `withAnyColor(BLACK, RED)`. The two
 * clauses run in order with [Effects.Composite]: exile the target, then Scry 1.
 */
val DevoutDecree = card("Devout Decree") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Exile target creature or planeswalker that's black or red. Scry 1. " +
        "(Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val victim = target(
            "target creature or planeswalker that's black or red",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.CreatureOrPlaneswalker.withAnyColor(Color.BLACK, Color.RED),
                ),
            ),
        )
        effect = Effects.Composite(
            Effects.Exile(victim),
            Effects.Scry(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "13"
        artist = "Zoltan Boros"
        flavorText = "\"It is not punishment. I am simply making things as they should be.\"\n—Sephara, Sky's Blade"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2dcde8fe-d4a4-4c6e-926e-c4a1b45045e4.jpg?1782708383"
    }
}
