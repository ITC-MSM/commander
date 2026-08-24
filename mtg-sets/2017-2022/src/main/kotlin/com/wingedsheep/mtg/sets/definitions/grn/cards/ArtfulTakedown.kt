package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Artful Takedown
 * {2}{U}{B}
 * Instant
 * Choose one or both —
 * • Tap target creature.
 * • Target creature gets -2/-4 until end of turn.
 */
val ArtfulTakedown = card("Artful Takedown") {
    manaCost = "{2}{U}{B}"
    colorIdentity = "BU"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n" +
        "• Tap target creature.\n" +
        "• Target creature gets -2/-4 until end of turn."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Tap target creature") {
                val creature = target("target", Targets.Creature)
                effect = Effects.Tap(creature)
            }
            mode("Target creature gets -2/-4 until end of turn") {
                val creature = target("target", Targets.Creature)
                effect = Effects.ModifyStats(-2, -4, creature)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "151"
        artist = "Mike Bierek"
        flavorText = "Dimir assassinations are choreographed like dance routines. Each challenge is anticipated and countered with graceful ease."
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c9e8f24-af62-4d13-bfed-a8b3294b64c3.jpg?1783934144"
    }
}
