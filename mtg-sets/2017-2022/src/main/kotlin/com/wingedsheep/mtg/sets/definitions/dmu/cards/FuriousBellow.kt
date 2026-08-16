package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Furious Bellow
 * {1}{R}
 * Instant
 * Target creature gets +3/+0 and gains first strike until end of turn. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val FuriousBellow = card("Furious Bellow") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+0 and gains first strike until end of turn. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.Composite(
                Effects.ModifyStats(3, 0, t),
                Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
            ),
            Effects.Scry(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "Joshua Raphael"
        flavorText = "Minotaurs sometimes pretend to be lost in a battle rage, leading their opponents to underestimate their cleverness."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63d05659-bfff-4b73-80f7-a9f31a7ef957.jpg?1783921317"
    }
}
