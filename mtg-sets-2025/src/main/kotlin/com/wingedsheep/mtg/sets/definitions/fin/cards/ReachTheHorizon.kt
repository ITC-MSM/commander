package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.SelectionRestriction
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Reach the Horizon
 * {3}{G}
 * Sorcery
 * Search your library for up to two basic land cards and/or Town cards with different
 * names, put them onto the battlefield tapped, then shuffle.
 *
 * Atomic pipeline: gather library basics/Towns → ChooseUpTo(2) with OnePerCardName
 * ("different names") → MoveCollection onto the battlefield tapped → shuffle. "Up to two"
 * means you may find fewer (or none).
 */
val ReachTheHorizon = card("Reach the Horizon") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Search your library for up to two basic land cards and/or Town cards with different names, put them onto the battlefield tapped, then shuffle."

    val basicOrTown = GameObjectFilter.BasicLand or GameObjectFilter.Land.withSubtype("Town")

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.LIBRARY, Player.You, basicOrTown),
                    storeAs = "searchable"
                ),
                SelectFromCollectionEffect(
                    from = "searchable",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(2)),
                    storeSelected = "found",
                    restrictions = listOf(SelectionRestriction.OnePerCardName),
                    prompt = "Search for up to two basic land or Town cards with different names"
                ),
                MoveCollectionEffect(
                    from = "found",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped)
                ),
                ShuffleLibraryEffect()
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "195"
        artist = "ikeda_cpt"
        flavorText = "\"Guess this is goodbye, City of Mako.\"\n—Tifa Lockhart"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c25960e0-5779-4e20-89f3-03950ad9d91c.jpg?1748706490"
    }
}
