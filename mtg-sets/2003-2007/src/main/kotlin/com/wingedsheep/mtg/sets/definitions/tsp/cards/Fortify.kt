package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fortify
 * {2}{W}
 * Instant
 *
 * Choose one —
 * • Creatures you control get +2/+0 until end of turn.
 * • Creatures you control get +0/+2 until end of turn.
 */
val Fortify = card("Fortify") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Creatures you control get +2/+0 until end of turn.\n• Creatures you control get +0/+2 until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Creatures you control get +2/+0 until end of turn") {
                effect = Effects.ForEachInGroup(
                    GroupFilter(GameObjectFilter.Creature.youControl()),
                    ModifyStatsEffect(2, 0, EffectTarget.Self),
                )
            }
            mode("Creatures you control get +0/+2 until end of turn") {
                effect = Effects.ForEachInGroup(
                    GroupFilter(GameObjectFilter.Creature.youControl()),
                    ModifyStatsEffect(0, 2, EffectTarget.Self),
                )
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Christopher Moeller"
        flavorText = "\"Where metal is tainted and wood is scarce, we are best armed by faith.\"\n—Tavalus, acolyte of Korlis"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd063dc7-a35c-44c9-9f8d-b7bb2dc95bec.jpg?1783943254"
    }
}
