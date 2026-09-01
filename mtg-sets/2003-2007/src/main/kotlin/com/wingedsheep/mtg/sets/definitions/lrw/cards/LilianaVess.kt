package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Liliana Vess - {3}{B}{B}
 * Legendary Planeswalker — Liliana
 * Starting Loyalty: 5
 *
 * +1: Target player discards a card.
 *
 * −2: Search your library for a card, then shuffle and put that card on top.
 *
 * −8: Put all creature cards from all graveyards onto the battlefield under your control.
 *
 * The +1 targets a *player*, who chooses what to discard. The −2 is the Vampiric Tutor shape —
 * [SearchDestination.TOP_OF_LIBRARY] shuffles first and then places the found card on top. The
 * −8 is the Rise of the Dark Realms gather: every graveyard ([Player.Each]) is read, and the
 * collection moves to the battlefield under the resolving player's control.
 */
val LilianaVess = card("Liliana Vess") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Planeswalker — Liliana"
    startingLoyalty = 5
    oracleText = "+1: Target player discards a card.\n" +
        "−2: Search your library for a card, then shuffle and put that card on top.\n" +
        "−8: Put all creature cards from all graveyards onto the battlefield under your control."

    // +1: Target player discards a card.
    loyaltyAbility(+1) {
        val player = target("target player", Targets.Player)
        effect = Effects.Discard(1, player)
    }

    // −2: Search your library for a card, then shuffle and put that card on top.
    loyaltyAbility(-2) {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any,
            destination = SearchDestination.TOP_OF_LIBRARY
        )
    }

    // −8: Put all creature cards from all graveyards onto the battlefield under your control.
    loyaltyAbility(-8) {
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(
                    zone = Zone.GRAVEYARD,
                    player = Player.Each,
                    filter = GameObjectFilter.Creature
                ),
                storeAs = "creatureCardsInAllGraveyards"
            ),
            MoveCollectionEffect(
                from = "creatureCardsInAllGraveyards",
                destination = CardDestination.ToZone(Zone.BATTLEFIELD)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "121"
        artist = "Aleksi Briclot"
        imageUri = "https://cards.scryfall.io/normal/front/1/6/1662fcb1-f142-43ff-b682-6827dea2ff7e.jpg?1783942888"
        ruling(
            "2009-10-01",
            "A \"creature card\" is any card with the type creature, even if it has other types " +
                "such as artifact, enchantment, or land. Older cards of type summon are also " +
                "creature cards.",
        )
    }
}
