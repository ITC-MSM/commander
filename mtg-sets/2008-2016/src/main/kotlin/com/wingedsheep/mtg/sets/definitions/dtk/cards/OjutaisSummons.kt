package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ojutai's Summons
 * {3}{U}{U}
 * Sorcery
 *
 * Create a 2/2 blue Djinn Monk creature token with flying.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * The blue Trumpeting Herd: one [Effects.CreateToken] plus the bare [Keyword.REBOUND], and the two
 * Djinn Monks arrive a turn apart because `StackResolver` re-casts the card from exile on your next
 * upkeep. The token's art comes from the set's token sheet, so no `imageUri` is spelled here.
 */
val OjutaisSummons = card("Ojutai's Summons") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Create a 2/2 blue Djinn Monk creature token with flying.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Djinn", "Monk"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Jakub Kasper"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/6769341a-1331-456d-a2bb-cd7fffe7b51d.jpg?1783938605"
    }
}
