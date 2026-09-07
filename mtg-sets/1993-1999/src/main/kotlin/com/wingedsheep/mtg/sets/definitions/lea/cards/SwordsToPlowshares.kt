package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Swords to Plowshares
 * {W}
 * Instant
 * Exile target creature. Its controller gains life equal to its power.
 *
 * The life gain ([DynamicAmounts.targetPower]) is sequenced *before* the exile, inverting
 * the printed order. This is a workaround, not a rules claim: target references read the
 * live board only, so a power read taken after the exile falls through to the card's printed
 * P/T and would ignore counters, Auras and lords. Gaining first reads the projected power and
 * controller while the creature is still on the battlefield, which is the value CR 608.2h and
 * the card's ruling ask for. Same house pattern as Crumble.
 *
 * The life gained is identical under either order. The one divergence is that a
 * leaves-the-battlefield trigger observes the post-gain board, which can only matter for a
 * creature whose power keys off a life total (Serra Ascendant, Serra Avatar).
 */
val SwordsToPlowshares = card("Swords to Plowshares") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Exile target creature. Its controller gains life equal to its power."

    spell {
        val creature = target(
            "target creature",
            TargetObject(filter = TargetFilter.Creature)
        )
        effect = Effects.GainLife(DynamicAmounts.targetPower(), EffectTarget.TargetController)
            .then(Effects.Exile(creature))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "40"
        artist = "Jeff A. Menges"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/386ea9eb-abc1-4862-aa2d-8fb808d79490.jpg?1783948709"
        ruling("2022-12-08", "Use the power of the creature from when it was last on the battlefield to determine how much life is gained.")
    }
}
