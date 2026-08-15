package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cartographer's Survey
 * {3}{G}
 * Sorcery
 *
 * Look at the top seven cards of your library. Put up to two land cards from among them onto the
 * battlefield tapped. Put the rest on the bottom of your library in a random order.
 *
 * Gather → Select → Move pipeline: privately look at the top seven ([GatherCardsEffect] with
 * `revealed = false`), let the caster put *up to two* land cards among them onto the battlefield
 * tapped ([SelectionMode.ChooseUpTo] filtered to [Filters.Land], `showAllCards` so the caster sees
 * every looked-at card but may only choose lands), and put the remainder on the bottom of the
 * library in a random order ([CardOrder.Random]).
 */
val CartographersSurvey = card("Cartographer's Survey") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Look at the top seven cards of your library. Put up to two land cards from among " +
        "them onto the battlefield tapped. Put the rest on the bottom of your library in a random order."

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(7)),
                    storeAs = "looked",
                    revealed = false
                ),
                SelectFromCollectionEffect(
                    from = "looked",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(2)),
                    filter = Filters.Land,
                    storeSelected = "toBattlefield",
                    storeRemainder = "rest",
                    showAllCards = true,
                    prompt = "Put up to two land cards onto the battlefield tapped",
                    selectedLabel = "Put onto the battlefield tapped",
                    remainderLabel = "Put on the bottom of your library"
                ),
                MoveCollectionEffect(
                    from = "toBattlefield",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped)
                ),
                MoveCollectionEffect(
                    from = "rest",
                    destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                    order = CardOrder.Random
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "190"
        artist = "Donato Giancola"
        flavorText = "She monitors the roads in every province, mapping safe routes through hunting " +
            "grounds and haunting grounds alike."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9a41cfc-f329-4e69-a785-835f69c7d2ba.jpg?1783924818"
    }
}
