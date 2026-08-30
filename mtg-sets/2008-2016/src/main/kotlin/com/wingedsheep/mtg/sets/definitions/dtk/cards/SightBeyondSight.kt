package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * Sight Beyond Sight
 * {3}{U}
 * Sorcery
 *
 * Look at the top two cards of your library. Put one of them into your hand and the other on the bottom of your library.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * Anticipate's pipeline with two cards instead of three: `lookAtTopAndKeep` gathers, offers the
 * choice, and moves both halves. "The other" is a single card, so no ordering is printed and
 * `restOrder` stays at its `Preserve` default — unlike Anticipate, whose "the rest … in any order"
 * needs `CardOrder.ControllerChooses`. The two selection labels are derived from the destinations.
 *
 * The second line is the bare [Keyword.REBOUND], engine-live in `StackResolver`.
 */
val SightBeyondSight = card("Sight Beyond Sight") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Look at the top two cards of your library. Put one of them into your hand and the other on the bottom of your library.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 2,
            keepCount = 1,
            keepDestination = CardDestination.ToZone(Zone.HAND),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "75"
        artist = "Anastasia Ovchinnikova"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/def0df83-fe20-497f-b372-1d2e4d7c8df9.jpg?1783938604"
    }
}
