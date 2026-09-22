package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val ChargeTheSanctum = card("Charge the Sanctum") {
    manaCost = "{2}{R/W}"
    colorIdentity = "RW"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Creatures you control get +2/+0 until end of turn.\n" +
        "• Target creature gets +2/+0 and gains first strike until end of turn. Put a +1/+1 counter on it."

    spell {
        modal(chooseCount = 1) {
            mode("Creatures you control get +2/+0 until end of turn") {
                effect = Effects.ForEachInGroup(
                    GroupFilter.AllCreaturesYouControl,
                    Effects.ModifyStats(2, 0, EffectTarget.Self)
                )
            }
            mode("Target creature gets +2/+0 and gains first strike until end of turn. Put a +1/+1 counter on it") {
                val t = target("target creature", Targets.Creature)
                effect = Effects.Composite(
                    Effects.ModifyStats(2, 0, t),
                    Effects.GrantKeyword(Keyword.FIRST_STRIKE, t),
                    Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
                )
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "125"
        artist = "Néstor Ossandón Leal"
        flavorText = "Deep in their hearts, the team believed what was left of Jace could still be reasoned with."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87b40df5-5c0a-41f5-a09c-a04f17066a91.jpg?1788521570"
        inBooster = false
    }
}
