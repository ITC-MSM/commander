package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Bond of Flourishing — War of the Spark #155 (canonical printing)
 * {1}{G}
 * Sorcery
 * Look at the top three cards of your library. You may reveal a permanent card from among them
 * and put it into your hand. Put the rest on the bottom of your library in any order. You gain
 * 3 life.
 *
 * The first two sentences are one point in the look-at-the-top family —
 * [Patterns.Library.lookAtTopRevealMatchingToHand], the "reveal it as it goes to hand" spelling.
 * Only the rest-order differs from that recipe's defaults: the Bond cycle says "in any order"
 * (the controller reorders, [CardOrder.ControllerChooses]) rather than the family's usual
 * random shuffle-back. The life gain is a second sentence and composes after it.
 */
val BondOfFlourishing = card("Bond of Flourishing") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Look at the top three cards of your library. You may reveal a permanent card " +
        "from among them and put it into your hand. Put the rest on the bottom of your library " +
        "in any order. You gain 3 life."

    spell {
        effect = Effects.Composite(
            Patterns.Library.lookAtTopRevealMatchingToHand(
                count = DynamicAmount.Fixed(3),
                filter = GameObjectFilter.Permanent,
                prompt = "You may reveal a permanent card from among them and put it into your hand",
                restOrder = CardOrder.ControllerChooses
            ),
            Effects.GainLife(3)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "Tyler Walpole"
        flavorText = "\"We agree that life should flourish. We disagree on the optimal number of limbs.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df901fdc-8672-44f5-ade5-7c4e0b5c5d81.jpg"
    }
}
