package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Prescient Chimera
 * {3}{U}{U}
 * Creature — Chimera
 * 3/4
 *
 * Flying
 * Whenever you cast an instant or sorcery spell, scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val PrescientChimera = card("Prescient Chimera") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Chimera"
    oracleText = "Flying\nWhenever you cast an instant or sorcery spell, scry 1. (Look at the top card of your library. You may put that card on the bottom.)"
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.Scry(1)
        description = "Whenever you cast an instant or sorcery spell, scry 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9342aba-e3aa-4210-ad72-e609c7c027b8.jpg?1783939794"
    }
}
