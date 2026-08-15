package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Don't Make a Sound
 * {1}{U}
 * Instant
 *
 * Counter target spell unless its controller pays {2}. If they do, surveil 2.
 *
 * The surveil is an `onPaid` rider on [Effects.CounterUnlessPays]: it happens only when
 * the spell's controller chooses to pay {2} (and the spell therefore resolves), not when
 * the spell is countered.
 */
val DontMakeASound = card("Don't Make a Sound") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell unless its controller pays {2}. If they do, surveil 2. " +
        "(Look at the top two cards of your library, then put any number of them into your " +
        "graveyard and the rest on top of your library in any order.)"

    spell {
        target("target spell", Targets.Spell)
        effect = Effects.CounterUnlessPays("{2}", onPaid = Patterns.Library.surveil(2))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Zezhou Chen"
        flavorText = "Jace kept his mouth shut, but he couldn't shake the feeling that the strange creatures could hear the screaming in his mind."
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5f42e59-7315-41cb-9c41-346e44f0c5fb.jpg?1726286037"
    }
}
