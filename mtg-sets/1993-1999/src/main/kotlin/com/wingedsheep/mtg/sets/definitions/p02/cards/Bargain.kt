package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bargain
 * {2}{W}
 * Sorcery
 * Target opponent draws a card.
 * You gain 7 life.
 *
 * Portal Second Age is the card's earliest real-expansion printing, so the canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives here.
 *
 * Both halves resolve in printed order; the opponent's draw is not a cost and not conditional on
 * the life gain, and the spell still gains you 7 life if the draw does nothing.
 */
val Bargain = card("Bargain") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Target opponent draws a card.\nYou gain 7 life."

    spell {
        val opponent = target("target", Targets.Opponent)
        effect = Effects.Composite(
            Effects.DrawCards(1, opponent),
            Effects.GainLife(7),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "14"
        artist = "Phil Foglio"
        flavorText = "Bargaining with a goblin is like trading with a child; both believe they already own everything."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de7f3064-a378-4ca4-99f5-b46518ddc43d.jpg?1783946493"
    }
}
