package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Wickerfolk Thresher
 * {3}{G}
 * Artifact Creature — Scarecrow
 * 5/4
 *
 * Delirium — Whenever this creature attacks, if there are four or more card types among cards in
 * your graveyard, look at the top card of your library. If it's a land card, you may put it onto
 * the battlefield. If you don't put the card onto the battlefield, put it into your hand.
 *
 * Delirium is an ability word (no rules meaning of its own); the attack trigger carries an
 * intervening-"if" gate of [Conditions.Delirium] (four+ distinct card types in your graveyard).
 * The payoff is the look-top / play-land-else-hand pipeline (Fecund Greenshell shape), except the
 * land enters untapped ([ZonePlacement.Default]). Card types counted are artifact, battle,
 * creature, enchantment, instant, kindred, land, planeswalker, sorcery — supertypes and subtypes
 * don't count.
 */
val WickerfolkThresher = card("Wickerfolk Thresher") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Scarecrow"
    power = 5
    toughness = 4
    oracleText = "Delirium — Whenever this creature attacks, if there are four or more card types " +
        "among cards in your graveyard, look at the top card of your library. If it's a land card, " +
        "you may put it onto the battlefield. If you don't put the card onto the battlefield, put " +
        "it into your hand."

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.Delirium()
        effect = Effects.Composite(
            listOf(
                // Look at the top card of your library.
                GatherCardsEffect(
                    source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                    storeAs = "looked",
                ),
                // Split into land and non-land.
                FilterCollectionEffect(
                    from = "looked",
                    filter = CollectionFilter.MatchesFilter(GameObjectFilter.Land),
                    storeMatching = "landCards",
                    storeNonMatching = "nonLandCards"
                ),
                // If it's a land, you may put it onto the battlefield; else it stays for hand.
                SelectFromCollectionEffect(
                    from = "landCards",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "toBattlefield",
                    storeRemainder = "landToHand",
                    selectedLabel = "Put onto the battlefield",
                    remainderLabel = "Put into your hand"
                ),
                MoveCollectionEffect(
                    from = "toBattlefield",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD)
                ),
                // If you don't put the card onto the battlefield, put it into your hand
                // (both the declined land and any non-land top card).
                MoveCollectionEffect(
                    from = "landToHand",
                    destination = CardDestination.ToZone(Zone.HAND)
                ),
                MoveCollectionEffect(
                    from = "nonLandCards",
                    destination = CardDestination.ToZone(Zone.HAND)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "207"
        artist = "WolfSkullJack"
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3a74892-20cd-47f7-b514-a4c7f14cca8b.jpg?1726286638"
    }
}
