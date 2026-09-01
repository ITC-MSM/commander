package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Soratami Cloudskater
 * {1}{U}
 * Creature — Moonfolk Rogue
 * 1 / 1
 *
 * Flying
 * {2}, Return a land you control to its owner's hand: Draw a card, then discard a card.
 *
 * The Moonfolk land-bounce cost is [Costs.ReturnToHand] over `GameObjectFilter.Land` composed with
 * the mana half; "you control" is carried by the atom's enumerator, not by the filter. The loot is
 * the ordinary draw-then-discard composition — [Effects.DrawCards] followed by
 * [Patterns.Hand.discardCards], whose Gather → Select → Move pipeline is what makes the discard a
 * real choice rather than a random one.
 */
val SoratamiCloudskater = card("Soratami Cloudskater") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Moonfolk Rogue"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "{2}, Return a land you control to its owner's hand: Draw a card, then discard a card."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.ReturnToHand(GameObjectFilter.Land))
        effect = Effects.Composite(
            Effects.DrawCards(1),
            Patterns.Hand.discardCards(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Michael Sutfin"
        flavorText = "\"You hide your actions from eyes on the ground, but nothing escapes the clouds.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e3d3024-bef2-4b50-ab84-8ae2a23cdf27.jpg?1783944321"
    }
}
