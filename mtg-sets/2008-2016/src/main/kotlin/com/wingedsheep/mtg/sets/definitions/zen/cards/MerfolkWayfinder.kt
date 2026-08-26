package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Merfolk Wayfinder
 * {2}{U}
 * Creature — Merfolk Scout
 * 1/2
 * Flying
 * When this creature enters, reveal the top three cards of your library. Put all Island cards revealed this way into your hand and the rest on the bottom of your library in any order.
 *
 * "In any order" on the remainder is [CardOrder.ControllerChooses]; the default for this
 * pattern is a random order, which is a different printed wording.
 */
val MerfolkWayfinder = card("Merfolk Wayfinder") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Scout"
    power = 1
    toughness = 2
    oracleText = "Flying\n" +
        "When this creature enters, reveal the top three cards of your library. Put all Island cards revealed this way into your hand and the rest on the bottom of your library in any order."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.revealTopPutAllMatchingToHand(
            count = DynamicAmount.Fixed(3),
            filter = GameObjectFilter.Land.withSubtype("Island"),
            restOrder = CardOrder.ControllerChooses,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "56"
        artist = "Christopher Moeller"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71ab86f3-aa75-40ce-a71e-90d40dad4bdc.jpg"
    }
}
