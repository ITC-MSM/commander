package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Witness the Future — Innistrad: Crimson Vow #90
 * {2}{U} · Sorcery · Uncommon
 * Artist: Anato Finnstark
 *
 * Target player shuffles up to four target cards from their graveyard into their library. You look
 * at the top four cards of your library, then put one of those cards into your hand and the rest
 * on the bottom of your library in a random order.
 *
 * First sentence: the Gaea's Blessing shape — an optional `count = 4` [TargetObject] over
 * [TargetFilter.CardInGraveyard], each target moved to its owner's library
 * ([ForEachTargetEffect] over [Effects.Move]) and the library shuffled afterwards. Choosing zero
 * cards is legal and still shuffles (Scryfall ruling, below), which is what `optional = true`
 * buys: the shuffle is an unconditional step of the pipeline, not a rider on the moves.
 *
 * Second sentence is [Patterns.Library.lookAtTopAndKeep] — look at four, one to hand, the
 * remainder to the bottom in [CardOrder.Random] order (the Memory Deluge recipe with different
 * numbers).
 */
val WitnessTheFuture = card("Witness the Future") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Target player shuffles up to four target cards from their graveyard into their " +
        "library. You look at the top four cards of your library, then put one of those cards " +
        "into your hand and the rest on the bottom of your library in a random order."

    spell {
        target = TargetObject(
            count = 4,
            optional = true,
            filter = TargetFilter.CardInGraveyard
        )
        effect = ForEachTargetEffect(
            effects = listOf(Effects.Move(EffectTarget.ContextTarget(0), Zone.LIBRARY))
        ).then(ShuffleLibraryEffect())
            .then(
                Patterns.Library.lookAtTopAndKeep(
                    count = 4,
                    keepCount = 1,
                    keepDestination = CardDestination.ToZone(Zone.HAND),
                    restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                    restOrder = CardOrder.Random
                )
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "90"
        artist = "Anato Finnstark"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0b3683f-a68b-458c-8f70-bba0f8779b8a.jpg?1783924875"
        ruling(
            "2021-11-19",
            "If you choose zero target cards or the cards are illegal targets as Witness the " +
                "Future resolves, the target player will still shuffle their library."
        )
        ruling(
            "2021-11-19",
            "If the player is an illegal target as Witness the Future resolves, the library won't " +
                "be shuffled and any targeted cards remain in the graveyard."
        )
    }
}
