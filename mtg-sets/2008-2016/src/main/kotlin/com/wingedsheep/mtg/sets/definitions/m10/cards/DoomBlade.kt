package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Doom Blade
 * {1}{B}
 * Instant
 *
 * Destroy target nonblack creature.
 *
 * - The colour restriction lives in the *target filter*, not the effect: an illegal target makes the
 *   spell fizzle on resolution, and a creature that becomes black after Doom Blade is cast is
 *   rechecked then (CR 608.2b), so it survives.
 * - Plain [Effects.Destroy] — no "can't be regenerated" rider, unlike its Ice Age ancestor
 *   Dark Banishing.
 */
val DoomBlade = card("Doom Blade") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target nonblack creature."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.notColor(Color.BLACK)))
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Chippy"
        flavorText = "The void is without substance but cuts like steel."
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6e19acff-f3dd-417a-a9ab-ea3e36c1ba61.jpg?1783942383"
    }
}
