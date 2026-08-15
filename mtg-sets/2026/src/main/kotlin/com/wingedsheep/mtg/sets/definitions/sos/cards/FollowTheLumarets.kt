package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
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
 * Follow the Lumarets — Secrets of Strixhaven #148
 * {1}{G} · Sorcery
 *
 * Infusion — Look at the top four cards of your library. You may reveal a creature or land card
 * from among them and put it into your hand. If you gained life this turn, you may instead reveal
 * two creature and/or land cards from among them and put them into your hand. Put the rest on the
 * bottom of your library in a random order.
 *
 * Infusion (SOS ability word, no keyword): the bonus is gated on `Conditions.YouGainedLifeThisTurn`.
 * It folds into the single reveal step as a conditional upper bound on how many creature/land cards
 * you may take — up to 2 if you gained life this turn, otherwise up to 1 — via
 * `SelectionMode.ChooseUpTo(DynamicAmount.Conditional(...))`. This is the look-at-top reveal pipeline
 * (`Patterns.Library.lookAtTopRevealMatchingToHand` shape) inlined so the keep count can be dynamic.
 */
val FollowTheLumarets = card("Follow the Lumarets") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Infusion — Look at the top four cards of your library. You may reveal a creature or " +
        "land card from among them and put it into your hand. If you gained life this turn, you may " +
        "instead reveal two creature and/or land cards from among them and put them into your hand. " +
        "Put the rest on the bottom of your library in a random order."

    spell {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(4)),
                    storeAs = "looked"
                ),
                SelectFromCollectionEffect(
                    from = "looked",
                    // Infusion: up to two creature/land cards if you gained life this turn, else up to one.
                    selection = SelectionMode.ChooseUpTo(
                        DynamicAmount.Conditional(
                            condition = Conditions.YouGainedLifeThisTurn,
                            ifTrue = DynamicAmount.Fixed(2),
                            ifFalse = DynamicAmount.Fixed(1)
                        )
                    ),
                    filter = GameObjectFilter.CreatureOrLand,
                    storeSelected = "kept",
                    storeRemainder = "rest",
                    prompt = "You may reveal creature and/or land cards to put into your hand.",
                    showAllCards = true
                ),
                MoveCollectionEffect(
                    from = "kept",
                    destination = CardDestination.ToZone(Zone.HAND),
                    revealed = true
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
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Olivier Bernard"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9488480-2b6c-40bc-a93e-29fb1292a2e4.jpg?1776990222"
    }
}
