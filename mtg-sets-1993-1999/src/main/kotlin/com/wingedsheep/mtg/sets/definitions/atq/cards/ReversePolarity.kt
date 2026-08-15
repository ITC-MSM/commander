package com.wingedsheep.mtg.sets.definitions.atq.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Reverse Polarity
 * {W}{W}
 * Instant
 * You gain X life, where X is twice the damage dealt to you so far this turn by artifacts.
 *
 * The per-turn "damage dealt to you by artifact sources" accumulator is tracked by the engine
 * (`TurnTracker.DAMAGE_RECEIVED_FROM_ARTIFACTS`); `Multiply(..., 2)` doubles it. Both combat and
 * non-combat artifact damage count; prevented damage does not, and damage from non-artifact
 * sources is excluded — so with no artifact damage this turn, X = 0.
 */
val ReversePolarity = card("Reverse Polarity") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "You gain X life, where X is twice the damage dealt to you so far this turn by artifacts."

    spell {
        effect = Effects.GainLife(
            DynamicAmount.Multiply(DynamicAmounts.damageReceivedFromArtifactsThisTurn(), 2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Justin Hampton"
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da7ed8ba-3886-4779-a9b3-6892a7ed3527.jpg?1562941146"
    }
}
