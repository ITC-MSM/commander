package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val FistfulOfForce = card("Fistful of Force") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn. Clash with an opponent. If you win, that creature gets an additional +2/+2 and gains trample until end of turn. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, creature).then(
            Patterns.Mechanic.clash(
                Effects.ModifyStats(2, 2, creature)
                    .then(Effects.GrantKeyword(Keyword.TRAMPLE, creature))
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "212"
        artist = "Ralph Horsley"
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f7f77c7e-d35f-477e-acc4-a393044319ea.jpg?1783942864"
    }
}
