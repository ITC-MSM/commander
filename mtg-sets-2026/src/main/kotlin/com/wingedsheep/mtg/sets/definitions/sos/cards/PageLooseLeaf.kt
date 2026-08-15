package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.core.Zone

/**
 * Page, Loose Leaf — Secrets of Strixhaven #250
 * {2} · Legendary Artifact Creature — Construct · 0/2
 *
 * {T}: Add {C}.
 * Grandeur — Discard another card named Page, Loose Leaf: Reveal cards from the top of your library
 * until you reveal an instant or sorcery card. Put that card into your hand and the rest on the
 * bottom of your library in a random order.
 *
 * Grandeur (CR 207.2c) is an ability word — flavor only, no rules meaning. The ability is a normal
 * activated ability whose cost is "discard another card named Page, Loose Leaf". The discard cost
 * draws from the controller's hand; the source on the battlefield is never a candidate, so "another"
 * is satisfied automatically (any *other* Page copy in hand is the discard fodder).
 *
 * The reveal half composes [GatherUntilMatchEffect] (walk the library until the first instant or
 * sorcery) + [RevealCollectionEffect] + two filtered [MoveCollectionEffect]s: the matched
 * instant/sorcery → hand, the rest → the bottom of the library in a random order ([CardOrder.Random]).
 * GatherUntilMatch stops at the first instant/sorcery, so "the rest" is exactly the non-matching
 * revealed cards.
 */
val PageLooseLeaf = card("Page, Loose Leaf") {
    manaCost = "{2}"
    typeLine = "Legendary Artifact Creature — Construct"
    power = 0
    toughness = 2
    oracleText = "{T}: Add {C}.\n" +
        "Grandeur — Discard another card named Page, Loose Leaf: Reveal cards from the top of your " +
        "library until you reveal an instant or sorcery card. Put that card into your hand and the " +
        "rest on the bottom of your library in a random order."

    // {T}: Add {C}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // Grandeur — Discard another card named Page, Loose Leaf: reveal until an instant/sorcery, take it.
    activatedAbility {
        cost = Costs.Discard(GameObjectFilter.Any.named("Page, Loose Leaf"))
        effect = Effects.Composite(
            GatherUntilMatchEffect(
                filter = GameObjectFilter.InstantOrSorcery,
                storeMatch = "spell",
                storeRevealed = "revealed",
            ),
            RevealCollectionEffect(from = "revealed"),
            // The matched instant/sorcery goes to hand.
            MoveCollectionEffect(
                from = "revealed",
                filter = GameObjectFilter.InstantOrSorcery,
                destination = CardDestination.ToZone(Zone.HAND),
            ),
            // The rest go on the bottom of the library in a random order.
            MoveCollectionEffect(
                from = "revealed",
                filter = GameObjectFilter(
                    cardPredicates = listOf(
                        CardPredicate.Not(CardPredicate.Or(listOf(CardPredicate.IsInstant, CardPredicate.IsSorcery)))
                    )
                ),
                destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                order = CardOrder.Random,
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "250"
        artist = "Michal Ivan"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c6fecfd-8241-4cf0-b1eb-19472b99e0ed.jpg?1775938744"
    }
}
