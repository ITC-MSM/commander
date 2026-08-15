package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fear of Isolation
 * {1}{U}
 * Enchantment Creature — Nightmare
 * 2/3
 * As an additional cost to cast this spell, return a permanent you control to its owner's hand.
 * Flying
 *
 * The bounce is modeled as an `AdditionalCost.ReturnToHand` (CR 601.2f — paid as the spell is cast,
 * before it goes on the stack), so the spell can't be cast without a permanent to return.
 */
val FearOfIsolation = card("Fear of Isolation") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Nightmare"
    oracleText = "As an additional cost to cast this spell, return a permanent you control to its " +
        "owner's hand.\nFlying"
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    additionalCost(Costs.additional.ReturnToHand())

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Irina Nordsol"
        flavorText = "On the second day after he'd been separated from his friends, Tino worried " +
            "they'd be panicking at his absence. By the fourth, he was certain they'd never cared " +
            "for him at all."
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69aa6054-8c59-4bbc-a283-adb453639786.jpg?1726286073"
    }
}
