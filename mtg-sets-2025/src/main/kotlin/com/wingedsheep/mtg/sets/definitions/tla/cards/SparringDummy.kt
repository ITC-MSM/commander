package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sparring Dummy
 * {1}{G}
 * Artifact Creature — Scarecrow
 * 1/3
 * Defender
 * {T}: Mill a card. You may put a land card milled this way into your hand. You gain 2 life
 * if a Lesson card is milled this way. (To mill a card, put the top card of your library into
 * your graveyard.)
 */
val SparringDummy = card("Sparring Dummy") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Scarecrow"
    power = 1
    toughness = 3
    oracleText = "Defender\n" +
        "{T}: Mill a card. You may put a land card milled this way into your hand. You gain 2 " +
        "life if a Lesson card is milled this way. (To mill a card, put the top card of your " +
        "library into your graveyard.)"

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.Composite(
            listOf(
                // Mill a card (gather top card into "milled", move it to the graveyard).
                Patterns.Library.mill(1),
                // You may put a land card milled this way into your hand.
                SelectFromCollectionEffect(
                    from = "milled",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    filter = GameObjectFilter.Land,
                    storeSelected = "toHand",
                    showAllCards = true,
                    prompt = "You may put a land card into your hand",
                    selectedLabel = "Put in hand",
                    remainderLabel = "Leave in graveyard"
                ),
                MoveCollectionEffect(
                    from = "toHand",
                    destination = CardDestination.ToZone(Zone.HAND)
                ),
                // You gain 2 life if a Lesson card is milled this way.
                ConditionalEffect(
                    condition = Conditions.CollectionContainsMatch(
                        "milled",
                        GameObjectFilter.Any.withSubtype(Subtype.LESSON)
                    ),
                    effect = Effects.GainLife(2)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "197"
        artist = "Gemi"
        flavorText = "\"I am Melon Lord! Mwahaha!\"\n—Toph, Melon Lord"
        imageUri = "https://cards.scryfall.io/normal/front/5/2/520f5a03-ff5a-43ed-8f15-cf24596d60cd.jpg?1768338009"
    }
}
