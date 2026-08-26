package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dissolve
 * {1}{U}{U}
 * Instant
 *
 * Counter target spell. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 *
 * The scry is not conditional on the counter resolving — it is a second sentence in the same effect,
 * so a countered-or-not-still-legal target does not gate it.
 */
val Dissolve = card("Dissolve") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        target = Targets.Spell
        effect = Effects.Composite(
            Effects.CounterSpell(),
            Effects.Scry(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "47"
        artist = "Wesley Burt"
        flavorText = "\"You thought only the gods could stop you?\""
        imageUri = "https://cards.scryfall.io/normal/front/9/9/992e8119-f933-4e54-bb04-e1cc78f7e87b.jpg"
    }
}
