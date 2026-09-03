package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Faerie Mechanist
 * {3}{U}
 * Artifact Creature — Faerie Artificer
 * 2 / 2
 * Flying
 * When this creature enters, look at the top three cards of your library. You may reveal an
 * artifact card from among them and put it into your hand. Put the rest on the bottom of your
 * library in any order.
 *
 * The dig is the shared [Patterns.Library.lookAtTopRevealMatchingToHand] recipe — Glint-Nest
 * Crane with a three-card pile. The pattern already supplies the optional single pick, the
 * artifact filter as an unselectable-but-visible mask over all three cards, the reveal as the
 * kept card moves to hand, and the bottom-of-library destination for the remainder; "in any
 * order" is the non-default [CardOrder.ControllerChooses].
 */
val FaerieMechanist = card("Faerie Mechanist") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Faerie Artificer"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, look at the top three cards of your library. You may reveal an artifact card from among them and put it into your hand. Put the rest on the bottom of your library in any order."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(3),
            filter = GameObjectFilter.Artifact,
            prompt = "You may reveal an artifact card from among them and put it into your hand",
            restOrder = CardOrder.ControllerChooses
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d350ebd7-afcf-4d67-8173-b07cad3fa9bc.jpg"
    }
}
