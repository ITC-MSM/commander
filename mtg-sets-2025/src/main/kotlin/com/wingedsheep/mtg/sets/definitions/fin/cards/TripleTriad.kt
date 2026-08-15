package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.GrantPlayWithoutPayingCostEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Triple Triad — Final Fantasy #166
 * {3}{R}{R}{R} · Enchantment
 *
 * At the beginning of your upkeep, each player exiles the top card of their library. Until end of
 * turn, you may play the card you own exiled this way and each other card exiled this way with
 * lesser mana value than it without paying their mana costs.
 *
 * Built from impulse atoms. The two exile groups are gathered separately so the "always playable"
 * card (yours) and the MV-filtered set (everyone else's) can be handled independently:
 *  - "mine": your top card → exile.
 *  - "others": each opponent's top card → exile.
 *  - "playableOthers": the others strictly below your card's mana value. MVs are non-negative
 *    integers, so "lesser than it" (< n) is modeled exactly as [CollectionFilter.ManaValueAtMost]
 *    of [DynamicAmount.Subtract](mineMV, 1) — which correctly excludes cards tied with yours.
 *  - Play permission until end of turn, free, is granted over "mine" (unconditionally) and
 *    "playableOthers": [GrantMayPlayFromExileEffect] (also permits lands) + [GrantPlayWithoutPayingCostEffect].
 *
 * Defaults are correct as-is: opponents' cards are cast by you (controller-controls) and go to
 * their owner's graveyard after resolving (exileAfterResolve = false). Same gather→exile idiom as
 * Alania's Pathmaker; the per-player [Player.Each]/[Player.EachOpponent] reveal mirrors Psychic Battle.
 */
val TripleTriad = card("Triple Triad") {
    manaCost = "{3}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, each player exiles the top card of their " +
        "library. Until end of turn, you may play the card you own exiled this way and each other " +
        "card exiled this way with lesser mana value than it without paying their mana costs."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            listOf(
                // Your top card → "mine"
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(count = DynamicAmount.Fixed(1), player = Player.You),
                    storeAs = "mine"
                ),
                MoveCollectionEffect(from = "mine", destination = CardDestination.ToZone(Zone.EXILE)),
                // Each opponent's top card → "others"
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(count = DynamicAmount.Fixed(1), player = Player.EachOpponent),
                    storeAs = "others"
                ),
                MoveCollectionEffect(from = "others", destination = CardDestination.ToZone(Zone.EXILE)),
                // Others with strictly lesser mana value than your card (< mineMV  ==  <= mineMV - 1)
                FilterCollectionEffect(
                    from = "others",
                    filter = CollectionFilter.ManaValueAtMost(
                        DynamicAmount.Subtract(DynamicAmount.StoredCardManaValue("mine"), DynamicAmount.Fixed(1))
                    ),
                    storeMatching = "playableOthers"
                ),
                // Until end of turn, play them without paying mana costs.
                GrantMayPlayFromExileEffect("mine", MayPlayExpiry.EndOfTurn),
                GrantPlayWithoutPayingCostEffect("mine"),
                GrantMayPlayFromExileEffect("playableOthers", MayPlayExpiry.EndOfTurn),
                GrantPlayWithoutPayingCostEffect("playableOthers")
            )
        )
        description = "At the beginning of your upkeep, each player exiles the top card of their " +
            "library. Until end of turn, you may play the card you own exiled this way and each " +
            "other card exiled this way with lesser mana value than it without paying their mana costs."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "166"
        artist = "Ben Wootten"
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d9a1de36-7f47-4b28-bb56-38d7e5bed82f.jpg?1782686477"
    }
}
