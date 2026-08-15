package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
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
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Paradox Surveyor — Secrets of Strixhaven #208
 * {G}{G/U}{U} · Creature — Elf Druid · 3/3
 *
 * Reach
 * When this creature enters, look at the top five cards of your library. You may reveal a land card
 * or a card with {X} in its mana cost from among them and put it into your hand. Put the rest on the
 * bottom of your library in a random order.
 *
 * "A card with {X} in its mana cost" is the new `CardPredicate.HasXInManaCost` (inspects the printed
 * cost's {X} symbol, not the computed mana value), OR'd with `IsLand`. The reveal-and-take is the
 * look-at-top filtered-keep / bottom pipeline (Gather top 5 → optional `ChooseUpTo(1)` filtered
 * Select → kept revealed to hand, rest to the bottom in a random order).
 */
val ParadoxSurveyor = card("Paradox Surveyor") {
    manaCost = "{G}{G/U}{U}"
    colorIdentity = "UG"
    typeLine = "Creature — Elf Druid"
    oracleText = "Reach\nWhen this creature enters, look at the top five cards of your library. " +
        "You may reveal a land card or a card with {X} in its mana cost from among them and put it " +
        "into your hand. Put the rest on the bottom of your library in a random order."
    power = 3
    toughness = 3

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(listOf(
            GatherCardsEffect(CardSource.TopOfLibrary(DynamicAmount.Fixed(5)), storeAs = "looked"),
            SelectFromCollectionEffect(
                from = "looked",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                filter = GameObjectFilter(
                    cardPredicates = listOf(
                        CardPredicate.Or(listOf(CardPredicate.IsLand, CardPredicate.HasXInManaCost))
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
        ))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "208"
        artist = "Elizabeth Peiró"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7cb1af2-0302-46ff-8303-ae9d07541a01.jpg?1775938445"
    }
}
