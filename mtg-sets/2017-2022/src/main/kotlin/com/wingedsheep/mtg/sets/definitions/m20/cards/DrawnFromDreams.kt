package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
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
 * Drawn from Dreams
 * {2}{U}{U}
 * Sorcery
 *
 * Look at the top seven cards of your library. Put two of them into your hand and the rest on
 * the bottom of your library in a random order.
 *
 * The keep is mandatory and unfiltered — [SelectionMode.ChooseExactly] over the gathered seven —
 * so the remainder that goes to the bottom is whatever wasn't chosen, in a random order.
 */
val DrawnFromDreams = card("Drawn from Dreams") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Look at the top seven cards of your library. Put two of them into your hand " +
        "and the rest on the bottom of your library in a random order."

    spell {
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(7)),
                storeAs = "looked"
            ),
            SelectFromCollectionEffect(
                from = "looked",
                selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(2)),
                storeSelected = "kept",
                storeRemainder = "rest",
                selectedLabel = "Put in hand",
                remainderLabel = "Put on bottom"
            ),
            MoveCollectionEffect(
                from = "kept",
                destination = CardDestination.ToZone(Zone.HAND)
            ),
            MoveCollectionEffect(
                from = "rest",
                destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                order = CardOrder.Random
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "56"
        artist = "Chris Seaman"
        flavorText = "\"From a sea of infinite possibilities, our choices create the future.\"\n—Mu Yanling"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/5677cd48-de9f-4827-94d7-8a2301945742.jpg"
    }
}
