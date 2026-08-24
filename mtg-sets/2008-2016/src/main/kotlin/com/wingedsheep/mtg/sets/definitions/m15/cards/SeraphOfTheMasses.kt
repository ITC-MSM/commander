package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seraph of the Masses
 * {5}{W}{W}
 * Creature — Angel
 * * / *
 * Convoke
 * Flying
 * Seraph of the Masses's power and toughness are each equal to the number of creatures you control.
 *
 * The `*`/`*` is a characteristic-defining ability, so it lives on the card's stats via
 * [dynamicStats] rather than in a static ability — it applies in every zone (CR 604.3).
 */
val SeraphOfTheMasses = card("Seraph of the Masses") {
    manaCost = "{5}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText =
        "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Flying\n" +
        "Seraph of the Masses's power and toughness are each equal to the number of creatures you control."

    dynamicStats(DynamicAmounts.creaturesYouControl())

    keywords(Keyword.CONVOKE, Keyword.FLYING)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "32"
        artist = "Zoltan Boros"
        imageUri = "https://cards.scryfall.io/normal/front/3/6/36c857a8-7df6-4a50-ae58-4aab76d7d58c.jpg?1783939197"
    }
}
