package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Zone
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
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Seismic Sense — {G} Sorcery — Lesson
 *
 * Look at the top X cards of your library, where X is the number of lands you control.
 * You may reveal a creature or land card from among them and put it into your hand.
 * Put the rest on the bottom of your library in a random order.
 *
 * X is computed at resolution from the number of lands you control. The "may reveal …
 * put it into your hand" clause is modelled as an optional (ChooseUpTo 1) selection over
 * the looked-at cards filtered to creature-or-land; declining keeps every card in the
 * remainder, which is bottomed in a random order.
 */
val SeismicSense = card("Seismic Sense") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Sorcery — Lesson"
    oracleText = "Look at the top X cards of your library, where X is the number of lands you control. You may reveal a creature or land card from among them and put it into your hand. Put the rest on the bottom of your library in a random order."

    spell {
        effect = Effects.Composite(
            GatherCardsEffect(
                CardSource.TopOfLibrary(
                    DynamicAmount.Count(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land)
                ),
                storeAs = "looked"
            ),
            SelectFromCollectionEffect(
                from = "looked",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                filter = GameObjectFilter(
                    cardPredicates = listOf(
                        CardPredicate.Or(listOf(CardPredicate.IsCreature, CardPredicate.IsLand))
                    )
                ),
                storeSelected = "kept",
                storeRemainder = "rest",
                selectedLabel = "Put in hand",
                remainderLabel = "Put on bottom"
            ),
            MoveCollectionEffect(from = "kept", destination = CardDestination.ToZone(Zone.HAND), revealed = true),
            MoveCollectionEffect(
                from = "rest",
                destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                order = CardOrder.Random
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "195"
        artist = "Jo Cordisco"
        flavorText = "\"Even though I was born blind, I've never had a problem seeing ... I feel the vibrations in the earth, and I can 'see' where everything is.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7b93c2a-c478-4734-8aca-03f3e9c11b20.jpg?1764121326"
    }
}
