package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Failed Inspection
 * {2}{U}{U}
 * Instant
 *
 * Counter target spell. Draw a card, then discard a card.
 *
 * "Draw, then discard" is ordered, not simultaneous — the drawn card is a legal discard — so the
 * discard is [Patterns.Hand.discardCards], a Gather → Select → Move pipeline that reads the hand
 * *after* the draw resolves rather than a pre-computed choice.
 */
val FailedInspection = card("Failed Inspection") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell. Draw a card, then discard a card."

    spell {
        target("target", Targets.Spell)
        effect = Effects.Composite(
            Effects.CounterSpell(),
            Effects.DrawCards(1),
            Patterns.Hand.discardCards(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Matt Stewart"
        flavorText = "The most dangerous thing an artificer can do is believe an invention is perfect."
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8900f91-cb17-4f99-a5ce-15819369beb8.jpg?1783937221"
    }
}
