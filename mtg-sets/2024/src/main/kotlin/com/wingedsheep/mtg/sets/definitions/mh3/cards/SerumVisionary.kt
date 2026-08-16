package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Serum Visionary
 * {2}{U}
 * Creature — Vedalken Wizard
 * 2/2
 *
 * When this creature enters, draw a card, then scry 2.
 *
 * "Draw, *then* scry" — the order is load-bearing (it's the reverse of the more common
 * Preordain-style scry-then-draw), so the two halves are sequenced with `.then(...)` rather than
 * bundled into an unordered composite: the drawn card is off the top before the scry looks at it.
 */
val SerumVisionary = card("Serum Visionary") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Vedalken Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, draw a card, then scry 2."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1).then(Patterns.Library.scry(2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Warren Mahy"
        flavorText = "\"Pre-criminal notions detected in the Sixth Precinct. The suspect will " +
            "confess when arrested in two hours.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08a587f5-5910-405e-8982-c889dbbc7f98.jpg?1783911288"
    }
}
