package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Geist Snatch
 * {2}{U}{U}
 * Instant
 *
 * Counter target creature spell. Create a 1/1 blue Spirit creature token with flying.
 *
 * The token is created whether or not the countered spell is still on the stack, so the two
 * sentences are siblings in one composite rather than a gated rider.
 */
val GeistSnatch = card("Geist Snatch") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target creature spell. Create a 1/1 blue Spirit creature token with flying."

    spell {
        target("target", Targets.CreatureSpell)
        effect = Effects.Composite(
            Effects.CounterSpell(),
            Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.BLUE),
                creatureTypes = setOf("Spirit"),
                keywords = setOf(Keyword.FLYING),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "Dan Murayama Scott"
        flavorText = "Angels and demons aren't the only ones listening to your prayers."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6dac5db-ef96-4bd5-aabc-e5ae2b95c8c3.jpg?1783940720"
    }
}
