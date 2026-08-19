package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Final Revels
 * {4}{B}
 * Sorcery
 * Choose one —
 * • All creatures get +2/+0 until end of turn.
 * • All creatures get -0/-2 until end of turn.
 *
 * "All creatures" is untargeted and symmetrical — a `ForEachInGroup` over every creature on the
 * battlefield, both players' included.
 */
val FinalRevels = card("Final Revels") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• All creatures get +2/+0 until end of turn.\n" +
        "• All creatures get -0/-2 until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("All creatures get +2/+0 until end of turn") {
                effect = Effects.ForEachInGroup(
                    GroupFilter(GameObjectFilter.Creature),
                    Effects.ModifyStats(2, 0, EffectTarget.Self)
                )
            }
            mode("All creatures get -0/-2 until end of turn") {
                effect = Effects.ForEachInGroup(
                    GroupFilter(GameObjectFilter.Creature),
                    Effects.ModifyStats(0, -2, EffectTarget.Self)
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "113"
        artist = "Omar Rayyan"
        flavorText = "One whiff of the sweet, pungent scent leads to euphoria—or to an early grave."
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99f3744a-71c4-4a54-9e1c-92420526b792.jpg?1783942891"
    }
}
