package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.EmitLibrarySearchedEventEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Hoarding Dragon (M11 #144)
 * {3}{R}{R}  Creature — Dragon  4/4
 *
 * Flying
 * When this creature enters, you may search your library for an artifact card, exile it,
 * then shuffle.
 * When this creature dies, you may put the exiled card into its owner's hand.
 *
 * Modeling: the exiled artifact is linked to Hoarding Dragon via `linkToSource = true`, so the
 * dies trigger can find it again through [CardSource.FromLinkedExile] even though the Dragon has
 * already left the battlefield. Both triggers are `optional` ("you may"): declining the ETB does
 * not shuffle; declining the dies trigger leaves the card exiled. The ETB search uses
 * `ChooseUpTo(1)` so the controller may search and find nothing (CR 701.23b).
 */
val HoardingDragon = card("Hoarding Dragon") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature enters, you may search your library for an artifact card, exile it, " +
        "then shuffle.\n" +
        "When this creature dies, you may put the exiled card into its owner's hand."

    keywords(Keyword.FLYING)

    // ETB: may search library for an artifact card, exile it (linked to this), then shuffle.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.LIBRARY, Player.You, GameObjectFilter.Artifact),
                    storeAs = "hoardSearchable"
                ),
                SelectFromCollectionEffect(
                    from = "hoardSearchable",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    storeSelected = "hoardFound",
                    prompt = "Search for an artifact card to exile"
                ),
                MoveCollectionEffect(
                    from = "hoardFound",
                    destination = CardDestination.ToZone(Zone.EXILE),
                    linkToSource = true
                ),
                ShuffleLibraryEffect(),
                EmitLibrarySearchedEventEffect
            )
        )
    }

    // Dies: may put the linked exiled card into its owner's hand.
    triggeredAbility {
        trigger = Triggers.Dies
        optional = true
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromLinkedExile(),
                    storeAs = "hoardExiled"
                ),
                MoveCollectionEffect(
                    from = "hoardExiled",
                    destination = CardDestination.ToZone(Zone.HAND)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "144"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f8b6932-e62d-4d38-bd0e-9ab8d4a56762.jpg?1782715390"
    }
}
