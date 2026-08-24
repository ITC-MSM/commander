package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Tradewind Rider
 * {3}{U}
 * Creature — Spirit
 * 1/4
 * Flying
 * {T}, Tap two untapped creatures you control: Return target permanent to its owner's hand.
 */
val TradewindRider = card("Tradewind Rider") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 4
    oracleText = "Flying\n" +
        "{T}, Tap two untapped creatures you control: Return target permanent to its owner's hand."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.TapPermanents(2, GameObjectFilter.Creature))
        val t = target("target", Targets.Permanent)
        effect = Effects.ReturnToHand(t)
        description = "{T}, Tap two untapped creatures you control: Return target permanent to its owner's hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "98"
        artist = "John Matson"
        flavorText = "It is said that the wind will blow the world past if you wait long enough."
        imageUri = "https://cards.scryfall.io/normal/front/0/9/09412374-3645-4644-952e-2beaefb3104b.jpg"
    }
}
