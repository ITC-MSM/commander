package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val VigorbloomCharm = card("Vigorbloom Charm") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Target permanent you control gains hexproof and indestructible until end of turn.\n" +
        "• You draw a card and gain 3 life.\n" +
        "• Put a +1/+1 counter on target creature you control. Then it fights target creature an opponent controls. (Each deals damage equal to its power to the other.)"

    spell {
        modal(chooseCount = 1) {
            mode("Target permanent you control gains hexproof and indestructible until end of turn") {
                val t = target("target permanent you control", Targets.PermanentYouControl)
                effect = Effects.Composite(
                    Effects.GrantKeyword(Keyword.HEXPROOF, t),
                    Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
                )
            }
            mode("You draw a card and gain 3 life") {
                effect = Effects.Composite(
                    Effects.DrawCards(1),
                    Effects.GainLife(3)
                )
            }
            mode("Put a +1/+1 counter on target creature you control. Then it fights target creature an opponent controls") {
                val yours = target("target creature you control", Targets.CreatureYouControl)
                val theirs = target("target creature an opponent controls", Targets.CreatureOpponentControls)
                effect = Effects.Composite(
                    Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, yours),
                    Effects.Fight(yours, theirs)
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "160"
        artist = "Kaitlyn McCulley"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b198e10-b507-4314-a29c-a219f06e48b7.jpg?1789127656"
        inBooster = false
    }
}
