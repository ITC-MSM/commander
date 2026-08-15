package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Flow State
 * {1}{U}
 * Sorcery
 *
 * Look at the top three cards of your library. Put one of them into your hand and the rest on
 * the bottom of your library in any order. If there is an instant card and a sorcery card in
 * your graveyard, instead put two of them into your hand and the rest on the bottom of your
 * library in any order.
 *
 * The dig is the standard look-at-top-and-keep pipeline. The number kept is conditional: 2 when
 * the graveyard holds both an instant card and a sorcery card, otherwise 1. The condition is
 * evaluated at resolution, so it reads the graveyard at the time the spell resolves.
 */
val FlowState = card("Flow State") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Look at the top three cards of your library. Put one of them into your hand and " +
        "the rest on the bottom of your library in any order. If there is an instant card and a " +
        "sorcery card in your graveyard, instead put two of them into your hand and the rest on " +
        "the bottom of your library in any order."

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(
            count = DynamicAmount.Fixed(3),
            keepCount = DynamicAmount.Conditional(
                condition = Conditions.All(
                    Conditions.GraveyardContains(Filters.Instant),
                    Conditions.GraveyardContains(Filters.Sorcery)
                ),
                ifTrue = DynamicAmount.Fixed(2),
                ifFalse = DynamicAmount.Fixed(1)
            ),
            keepDestination = CardDestination.ToZone(Zone.HAND),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.ControllerChooses
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "49"
        artist = "Genel Jumalon"
        imageUri = "https://cards.scryfall.io/normal/front/4/7/47d6093b-b1b6-4956-8bfd-02cce899f832.jpg?1775937249"
    }
}
