package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Shadowfeed
 * {B}
 * Instant
 * Exile target card from a graveyard. You gain 3 life.
 *
 * "A graveyard" is any graveyard, so the target is [TargetFilter.CardInGraveyard] — an unrestricted
 * object filter scoped to [Zone.GRAVEYARD] — and the exile is [Effects.Move] to [Zone.EXILE]. The
 * life gain is a second, untargeted clause, so it is composed after the move and resolves even if
 * the card has already left the graveyard.
 */
val Shadowfeed = card("Shadowfeed") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target card from a graveyard. You gain 3 life."

    spell {
        val t = target("target", TargetObject(filter = TargetFilter.CardInGraveyard))
        effect = Effects.Composite(
            Effects.Move(t, Zone.EXILE),
            Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Dave Kendall"
        flavorText = "\"The future is a snake, devouring your life backwards through time. And when you die, believe me, it doesn't stop feeding.\"\n—Sedris, the Traitor King"
        imageUri = "https://cards.scryfall.io/normal/front/1/9/19ccd8c7-7472-47df-9d3e-1b5fb1431118.jpg"
    }
}
