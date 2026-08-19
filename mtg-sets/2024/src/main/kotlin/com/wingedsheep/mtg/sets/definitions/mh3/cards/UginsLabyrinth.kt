package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ugin's Labyrinth — Modern Horizons 3 #233
 * Land · Mythic
 *
 * Imprint — When this land enters, you may exile a colorless card with mana value 7 or greater
 * from your hand.
 * {T}: Add {C}. If a card is exiled with this land, add {C}{C} instead.
 * {T}: Return the exiled card to its owner's hand.
 *
 * Modeling notes:
 *  - "Imprint" (CR 207.2c) is an ability word here, not a rules keyword — it just labels the ETB
 *    exile-and-link ability, same as the SDK's other linked-exile cards (Clive's Hideaway).
 *  - The ETB gathers hand cards already filtered to colorless + mana value 7 or greater, then
 *    offers an optional (`ChooseUpTo(1)`) pick — modelling "you may exile a [filter] card" as a
 *    gather-then-optional-select rather than a mandatory `EntersWithChoice`, since declining is
 *    legal and leaves the hand untouched. The picked card is exiled linked to this land
 *    ([MoveCollectionEffect.linkToSource]).
 *  - The mana ability reads back [ContextPropertyKey.LINKED_EXILE_CARD_COUNT] (0 normally, 1 once
 *    a card is imprinted) via `1 + min(count, 1)`, so it's {C} until something is exiled and {C}{C}
 *    from then on — exactly "If a card is exiled with this land, add {C}{C} instead," and it stays
 *    that way even after a subsequent activation of the third ability (Ugin's Labyrinth has no way
 *    to un-link the card short of the third ability actually returning it).
 *  - The third ability is [Patterns.Exile.returnLinkedExileToHand] — a no-op if nothing is linked
 *    (legal to activate, per the 2024-06-07 ruling it returns *every* linked card if more than one
 *    was ever exiled, e.g. via a copied trigger).
 */
private val imprintFilter = GameObjectFilter(cardPredicates = listOf(CardPredicate.IsColorless))
    .manaValueAtLeast(7)

val UginsLabyrinth = card("Ugin's Labyrinth") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "Imprint — When this land enters, you may exile a colorless card with mana " +
        "value 7 or greater from your hand.\n" +
        "{T}: Add {C}. If a card is exiled with this land, add {C}{C} instead.\n" +
        "{T}: Return the exiled card to its owner's hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.HAND, Player.You, imprintFilter),
                    storeAs = "labyrinthEligible"
                ),
                SelectFromCollectionEffect(
                    from = "labyrinthEligible",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "labyrinthPicked",
                    prompt = "Exile a colorless card with mana value 7 or greater?",
                    selectedLabel = "Exile"
                ),
                MoveCollectionEffect(
                    from = "labyrinthPicked",
                    destination = CardDestination.ToZone(Zone.EXILE),
                    linkToSource = true
                )
            )
        )
        description = "Imprint — When this land enters, you may exile a colorless card with " +
            "mana value 7 or greater from your hand."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(
            DynamicAmount.Add(
                DynamicAmount.Fixed(1),
                DynamicAmount.Min(
                    DynamicAmount.ContextProperty(ContextPropertyKey.LINKED_EXILE_CARD_COUNT),
                    DynamicAmount.Fixed(1)
                )
            )
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {C}. If a card is exiled with this land, add {C}{C} instead."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Patterns.Exile.returnLinkedExileToHand()
        description = "{T}: Return the exiled card to its owner's hand."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "233"
        artist = "Mark Poole"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/020e1348-1a35-4cc8-bad6-9fbddfa79277.jpg?1783911233"
        ruling(
            "2024-06-07",
            "In the rare case where more than one card is exiled with Ugin's Labyrinth's imprint " +
                "ability (likely because the triggered ability was copied or the ability triggered " +
                "a second time), its last ability will return all such cards to their owners' hands."
        )
    }
}
