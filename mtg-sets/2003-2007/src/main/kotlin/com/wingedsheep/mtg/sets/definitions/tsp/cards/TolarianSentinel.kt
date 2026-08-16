package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tolarian Sentinel
 * {3}{U}
 * Creature — Human Spellshaper
 * 1/3
 * Flying
 * {U}, {T}, Discard a card: Return target permanent you control to its owner's hand.
 */
val TolarianSentinel = card("Tolarian Sentinel") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Spellshaper"
    power = 1
    toughness = 3
    oracleText = "Flying\n{U}, {T}, Discard a card: Return target permanent you control to its owner's hand."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap, Costs.DiscardCard)
        val permanent = target("target permanent you control", Targets.PermanentYouControl)
        effect = Effects.ReturnToHand(permanent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Thomas M. Baxa"
        flavorText = "\"It is not just our people I try to rescue. It is our culture, and our hope that we can return to greatness.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e97ec8b-6163-41ff-9e6f-af091a7c529e.jpg?1783943238"
    }
}
