package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val TheorixCharm = card("Theorix Charm") {
    manaCost = "{U}{B}"
    colorIdentity = "UB"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Counter target noncreature spell unless its controller pays {2}.\n" +
        "• Target creature gets -2/-2 until end of turn.\n" +
        "• Mill three cards, then draw a card. (To mill three cards, put the top three cards of your library into your graveyard.)"

    spell {
        modal(chooseCount = 1) {
            mode("Counter target noncreature spell unless its controller pays {2}") {
                target = Targets.NoncreatureSpell
                effect = Effects.CounterUnlessPays("{2}")
            }
            mode("Target creature gets -2/-2 until end of turn") {
                val t = target("target creature", Targets.Creature)
                effect = Effects.ModifyStats(-2, -2, t)
            }
            mode("Mill three cards, then draw a card") {
                effect = Effects.Composite(
                    Patterns.Library.mill(3),
                    Effects.DrawCards(1)
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Elliot Lang"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/2835c9aa-0904-44db-8da2-e8c4e04201aa.jpg?1789127659"
        inBooster = false
    }
}
