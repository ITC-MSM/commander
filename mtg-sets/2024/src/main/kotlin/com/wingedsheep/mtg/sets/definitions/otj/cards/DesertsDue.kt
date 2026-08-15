package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Desert's Due
 * {1}{B}
 * Instant
 *
 * Target creature gets -2/-2 until end of turn. It gets an additional -1/-1 until end of turn
 * for each Desert you control.
 *
 * The base -2 and the per-Desert -1 collapse into a single -(2 + Deserts) stat modification
 * applied to power and toughness; the Desert count is read from the battlefield at resolution.
 */
val DesertsDue = card("Desert's Due") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -2/-2 until end of turn. It gets an additional -1/-1 " +
        "until end of turn for each Desert you control."

    spell {
        val creature = target("creature", Targets.Creature)
        val deserts = DynamicAmount.Count(
            Player.You,
            Zone.BATTLEFIELD,
            GameObjectFilter.Land.withSubtype(Subtype("Desert"))
        )
        val total = DynamicAmount.Multiply(DynamicAmount.Add(DynamicAmount.Fixed(2), deserts), -1)
        effect = Effects.ModifyStats(total, total, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "David Palumbo"
        flavorText = "He was glad when the mirages appeared. At least he wouldn't die alone."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/35e899e4-e2de-44cf-b6f4-cace8d3770cb.jpg?1712355575"
    }
}
