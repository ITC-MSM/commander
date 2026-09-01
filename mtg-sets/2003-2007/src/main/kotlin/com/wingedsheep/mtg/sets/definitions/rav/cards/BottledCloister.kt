package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Bottled Cloister — Ravnica: City of Guilds #256
 * {4} · Artifact
 *
 * At the beginning of each opponent's upkeep, exile all cards from your hand face down.
 * At the beginning of your upkeep, return all cards you own exiled with this artifact to your
 * hand, then draw a card.
 *
 * The two halves are a *linked* pair (CR 607): the exile writes the cards onto the Cloister's own
 * `LinkedExileComponent` (`linkToSource = true`, the Shared Fate shape) and the upkeep half reads
 * that same pile back with `CardSource.FromLinkedExile`. That linkage is what makes "exiled with
 * this artifact" mean *this* Cloister's pile and not every card in exile.
 *
 * [FaceDownMode.HIDDEN] is a face-down exile with no turn-up: opponents can't read the hand they
 * just made you put away.
 *
 * The `ownedByYou()` filter on the return is the 2006 errata ("only the cards you own go into your
 * hand"): if control of the Cloister changes mid-turn the pile can hold two players' cards, and
 * only the current controller's own cards come back. Everything left in the pile stays exiled —
 * including everything in it if the Cloister leaves the battlefield before your upkeep.
 */
val BottledCloister = card("Bottled Cloister") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "At the beginning of each opponent's upkeep, exile all cards from your hand face down.\n" +
        "At the beginning of your upkeep, return all cards you own exiled with this artifact to your " +
        "hand, then draw a card."

    triggeredAbility {
        trigger = Triggers.EachOpponentUpkeep
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(Zone.HAND, Player.You, GameObjectFilter.Any),
                storeAs = "cloisterHand"
            ),
            MoveCollectionEffect(
                from = "cloisterHand",
                destination = CardDestination.ToZone(Zone.EXILE),
                faceDown = FaceDownMode.HIDDEN,
                linkToSource = true
            )
        )
    }

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromLinkedExile(),
                storeAs = "cloisterExiled"
            ),
            FilterCollectionEffect(
                from = "cloisterExiled",
                filter = CollectionFilter.MatchesFilter(GameObjectFilter.Any.ownedByYou()),
                storeMatching = "cloisterMine"
            ),
            MoveCollectionEffect(
                from = "cloisterMine",
                destination = CardDestination.ToZone(Zone.HAND)
            ),
            // "then draw a card" — unconditional; it happens even with an empty pile.
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "256"
        artist = "Luca Zontini"
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5bf42acc-d2d3-4165-b257-6885df2d1ba4.jpg?1783943601"

        ruling(
            "2006-02-01",
            "Errata'd to make it clear that only the cards you own go into your hand, so if you " +
                "somehow gain control of the Cloister, your opponent's cards won't go to your hand."
        )
    }
}
