package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Turf Wound
 * {2}{R}
 * Instant
 *
 * Target player can't play lands this turn.
 * Draw a card.
 */
val TurfWound = card("Turf Wound") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target player can't play lands this turn.\nDraw a card."

    spell {
        target = Targets.Player
        effect = Effects.Composite(
            Effects.CantPlayLandsThisTurn(EffectTarget.ContextTarget(0)),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Thomas Gianni"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/91392e9f-f96a-4ac5-b1f1-c73540cf249e.jpg?1562924301"
    }
}
