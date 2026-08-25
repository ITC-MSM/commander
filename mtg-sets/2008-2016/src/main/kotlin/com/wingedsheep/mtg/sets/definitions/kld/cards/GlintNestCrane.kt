package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Glint-Nest Crane
 * {1}{U}
 * Creature — Bird
 * 1/3
 * Flying
 * When this creature enters, look at the top four cards of your library. You may reveal an
 * artifact card from among them and put it into your hand. Put the rest on the bottom of your
 * library in any order.
 *
 * The dig is the shared [Patterns.Library.lookAtTopRevealMatchingToHand] recipe; "in any order"
 * is the non-default [CardOrder.ControllerChooses] for the remainder.
 */
val GlintNestCrane = card("Glint-Nest Crane") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird"
    oracleText = "Flying\n" +
        "When this creature enters, look at the top four cards of your library. You may reveal an artifact card from among them and put it into your hand. Put the rest on the bottom of your library in any order."
    power = 1
    toughness = 3
    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(4),
            filter = GameObjectFilter.Artifact,
            prompt = "You may reveal an artifact card from among them and put it into your hand",
            restOrder = CardOrder.ControllerChooses
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "Christopher Moeller"
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6fa5b030-23a6-4fca-b318-c580e3ea2bad.jpg?1783937219"
    }
}
