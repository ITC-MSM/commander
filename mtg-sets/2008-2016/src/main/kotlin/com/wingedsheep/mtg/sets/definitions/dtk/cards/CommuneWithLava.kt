package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Commune with Lava
 * {X}{R}{R}
 * Instant
 *
 * Exile the top X cards of your library. Until the end of your next turn, you may play those cards.
 *
 * The impulse-draw recipe with a dynamic count: [Patterns.Exile.impulse] gathers the top X, moves
 * that same stored collection to exile, and grants play permission over it — one variable name
 * threaded through all three steps, which is what makes "those cards" refer to the exact cards
 * exiled. "Until the end of your next turn" is
 * [MayPlayExpiry.UntilControllerStep] at the cleanup step with `includeCurrentTurn = false`, so the
 * permission survives this turn even when the spell is cast on your own turn.
 */
val CommuneWithLava = card("Commune with Lava") {
    manaCost = "{X}{R}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Exile the top X cards of your library. Until the end of your next turn, you may play those cards."

    spell {
        effect = Patterns.Exile.impulse(
            count = DynamicAmount.XValue,
            expiry = MayPlayExpiry.UntilControllerStep(Step.CLEANUP, includeCurrentTurn = false)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "131"
        artist = "Ryan Barger"
        flavorText = "Atarka conquered Qadat, the Fire Rim, long ago, winning over its efreet with a promise to spread the glory of fire to all the world."
        imageUri = "https://cards.scryfall.io/normal/front/0/5/051141cb-562a-42a5-984b-a83ee8baca51.jpg?1783938591"
    }
}
