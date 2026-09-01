package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fall of the Gavel
 * {3}{W}{U}
 * Instant
 *
 * Counter target spell. You gain 5 life.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * [Effects.CounterSpell] reads the spell from the bound target itself, so the composite's second
 * half still runs when the counter does nothing (the spell left the stack another way).
 */
val FallOfTheGavel = card("Fall of the Gavel") {
    manaCost = "{3}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Instant"
    oracleText = "Counter target spell. You gain 5 life."

    spell {
        val t = target("target spell", Targets.Spell)
        effect = Effects.Composite(
            Effects.CounterSpell(),
            Effects.GainLife(5),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "161"
        artist = "Matt Stewart"
        flavorText = "\"My ruling is final. Order is upheld. Justice is done.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64f42848-963b-4b16-aeec-66d0f349758b.jpg?1783940341"
    }
}
