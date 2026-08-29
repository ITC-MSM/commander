package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sylvan Yeti
 * {2}{G}{G}
 * Creature — Yeti
 *
 * Sylvan Yeti's power is equal to the number of cards in your hand.
 *
 * A characteristic-defining ability (CR 604.3): the starred power is the P/T slot, so it is a
 * `dynamicPower(...)` over the controller's hand size rather than an entry in a `CardScript`.
 * Toughness stays a printed 4.
 */
val SylvanYeti = card("Sylvan Yeti") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Yeti"
    oracleText = "Sylvan Yeti's power is equal to the number of cards in your hand."
    toughness = 4

    dynamicPower(DynamicAmounts.cardsInYourHand())

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "147"
        artist = "Brom"
        flavorText = "The deeper the wood, the greater its strength."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a8686e2-1e14-4b4a-b45b-cab4d5c57fce.jpg"
    }
}
