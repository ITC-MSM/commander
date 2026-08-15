package com.wingedsheep.mtg.sets.definitions.lci.cards

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
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Sinuous Benthisaur
 * {5}{U}
 * Creature — Dinosaur
 * 4/4
 *
 * When this creature enters, look at the top X cards of your library, where X is the number of
 * Caves you control plus the number of Cave cards in your graveyard. Put two of those cards into
 * your hand and the rest on the bottom of your library in a random order.
 *
 * X (evaluated at resolution, same pattern as Calamitous Cave-In / Gargantuan Leech):
 *   - Battlefield: permanents with the Cave land subtype you control
 *     ([DynamicAmount.Count] over [Zone.BATTLEFIELD], [GameObjectFilter.Land.withSubtype]("Cave"))
 *   - Graveyard:   any card with the Cave subtype in your graveyard
 *     ([DynamicAmount.Count] over [Zone.GRAVEYARD], [GameObjectFilter.Any.withSubtype]("Cave"))
 *   summed via [DynamicAmount.Add].
 *
 * Dig pipeline (private look — no reveal — then keep two, rest to bottom in random order):
 *   1. GatherCards(TopOfLibrary(X)) → "looked"         — pulled off the library so the controller
 *                                                         sees them privately during selection.
 *   2. SelectFromCollection("looked", ChooseExactly(2)) — the mandatory "put two into your hand".
 *        storeSelected = "kept", storeRemainder = "rest". ChooseExactly caps at the collection
 *        size, so with X < 2 the controller simply keeps everything looked at.
 *   3. MoveCollection("kept" → HAND) — not revealed; the whole look is private.
 *   4. MoveCollection("rest" → LIBRARY Bottom, order = CardOrder.Random) — "on the bottom of your
 *        library in a random order"; CardOrder.Random shuffles the moved cards and strips their
 *        reveal markers so the controller has no knowledge of their order.
 *
 * LCI #76, John Tedrick.
 */
val SinuousBenthisaur = card("Sinuous Benthisaur") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Dinosaur"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, look at the top X cards of your library, where X is " +
        "the number of Caves you control plus the number of Cave cards in your graveyard. Put two " +
        "of those cards into your hand and the rest on the bottom of your library in a random order."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield

        val cavesControlled = DynamicAmount.Count(
            player = Player.You,
            zone = Zone.BATTLEFIELD,
            filter = GameObjectFilter.Land.withSubtype("Cave"),
        )
        val cavesInGraveyard = DynamicAmount.Count(
            player = Player.You,
            zone = Zone.GRAVEYARD,
            filter = GameObjectFilter.Any.withSubtype("Cave"),
        )

        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(
                        DynamicAmount.Add(cavesControlled, cavesInGraveyard)
                    ),
                    storeAs = "looked"
                ),
                SelectFromCollectionEffect(
                    from = "looked",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(2)),
                    storeSelected = "kept",
                    storeRemainder = "rest",
                    selectedLabel = "Put into hand",
                    remainderLabel = "Put on bottom",
                    prompt = "Put two of those cards into your hand"
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
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "76"
        artist = "John Tedrick"
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a58639c-001f-4cb1-89fd-0a0967b86977.jpg?1782694549"
    }
}
