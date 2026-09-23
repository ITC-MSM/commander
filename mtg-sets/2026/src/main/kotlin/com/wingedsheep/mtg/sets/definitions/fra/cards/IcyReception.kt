package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

val IcyReception = card("Icy Reception") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Counter target creature or legendary spell unless its controller pays {3}.\n" +
        "• Target creature gets -5/-0 until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Counter target creature or legendary spell unless its controller pays {3}") {
                target(
                    "target creature or legendary spell",
                    TargetSpell(
                        filter = TargetFilter(
                            GameObjectFilter.Creature or GameObjectFilter.Any.legendary(),
                            zone = Zone.STACK
                        )
                    )
                )
                effect = Effects.CounterUnlessPays("{3}")
            }
            mode("Target creature gets -5/-0 until end of turn") {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.ModifyStats(-5, 0, creature)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Borja Pindado"
        flavorText = "\"Keep them distracted,\" instructed the Theorist. Chandra's doppelganger smiled, and a chill went through the air."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d754b96-5e44-45af-9c7a-b0da59fbe4c3.jpg?1788779540"
        inBooster = false
    }
}
