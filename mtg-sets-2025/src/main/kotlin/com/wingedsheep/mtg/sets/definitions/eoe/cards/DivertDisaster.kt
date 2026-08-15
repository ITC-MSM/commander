package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Divert Disaster
 * {1}{U}
 * Instant
 *
 * Counter target spell unless its controller pays {2}. If they do, you create a Lander token.
 */
val DivertDisaster = card("Divert Disaster") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell unless its controller pays {2}. If they do, you create a Lander token. " +
        "(It's an artifact with \"{2}, {T}, Sacrifice this token: Search your library for a basic land card, put it onto the battlefield tapped, then shuffle.\")"

    spell {
        target = Targets.Spell
        effect = Effects.CounterUnlessPays(
            cost = "{2}",
            onPaid = Effects.CreateLander()
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "David Álvarez"
        flavorText = "Illvoi hope for the best outcomes but engineer for the worst."
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c5f7d2fe-628b-4493-8281-0e5f91ce5d61.jpg?1752946770"
    }
}
