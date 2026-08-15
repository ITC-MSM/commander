package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ignis Scientia
 * {1}{G}{U}
 * Legendary Creature — Human Advisor
 * 2/2
 * When Ignis Scientia enters, look at the top six cards of your library. You may put a land
 * card from among them onto the battlefield tapped. Put the rest on the bottom of your library
 * in a random order.
 * I've Come Up with a New Recipe! — {1}{G}{U}, {T}: Exile target card from a graveyard. If a
 * creature card was exiled this way, create a Food token.
 */
val IgnisScientia = card("Ignis Scientia") {
    manaCost = "{1}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Human Advisor"
    oracleText = "When Ignis Scientia enters, look at the top six cards of your library. You may put a land card from among them onto the battlefield tapped. Put the rest on the bottom of your library in a random order.\nI've Come Up with a New Recipe! — {1}{G}{U}, {T}: Exile target card from a graveyard. If a creature card was exiled this way, create a Food token."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                // Look at the top six cards of your library.
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(count = DynamicAmount.Fixed(6), player = Player.You),
                    storeAs = "looked"
                ),
                // You may put a land card from among them onto the battlefield tapped.
                SelectFromCollectionEffect(
                    from = "looked",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    filter = GameObjectFilter.Land,
                    showAllCards = true,
                    storeSelected = "toBattlefield",
                    storeRemainder = "toBottom",
                    prompt = "You may put a land card onto the battlefield tapped",
                    selectedLabel = "Put onto the battlefield tapped",
                    remainderLabel = "Put on the bottom of your library"
                ),
                MoveCollectionEffect(
                    from = "toBattlefield",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, Player.You, ZonePlacement.Tapped)
                ),
                // Put the rest on the bottom of your library in a random order.
                MoveCollectionEffect(
                    from = "toBottom",
                    destination = CardDestination.ToZone(Zone.LIBRARY, Player.You, ZonePlacement.Bottom),
                    order = CardOrder.Random
                )
            )
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}{U}"), Costs.Tap)
        target("target", TargetObject(filter = TargetFilter.CardInGraveyard))
        effect = Effects.Composite(
            listOf(
                // Gather the targeted graveyard card so we can both exile it and test its type.
                GatherCardsEffect(source = CardSource.ChosenTargets, storeAs = "exiled"),
                // "If a creature card was exiled this way, create a Food token." The targeted card
                // is always exiled, so its type (read from the gathered collection, base card type is
                // zone-independent) determines the Food. Evaluated before the move so the collection's
                // entity ids are still live.
                ConditionalEffect(
                    condition = Conditions.CollectionContainsMatch("exiled", GameObjectFilter.Creature),
                    effect = Effects.CreateFood()
                ),
                // Exile target card from a graveyard.
                MoveCollectionEffect(
                    from = "exiled",
                    destination = CardDestination.ToZone(Zone.EXILE)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "227"
        artist = "Mingchen Shen"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab4f9721-5b2c-4371-98a5-3f6714265e57.jpg?1748706619"
    }
}
