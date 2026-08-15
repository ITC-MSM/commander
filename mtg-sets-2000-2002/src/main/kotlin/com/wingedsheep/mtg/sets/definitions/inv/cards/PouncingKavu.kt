package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.EntersWithKeywords
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Pouncing Kavu
 * {1}{R}
 * Creature — Kavu
 * 1/1
 * Kicker {2}{R}
 * First strike
 * If this creature was kicked, it enters with two +1/+1 counters on it and with haste.
 */
val PouncingKavu = card("Pouncing Kavu") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu"
    power = 1
    toughness = 1
    oracleText = "Kicker {2}{R} (You may pay an additional {2}{R} as you cast this spell.)\n" +
        "First strike\n" +
        "If this creature was kicked, it enters with two +1/+1 counters on it and with haste."

    keywordAbility(KeywordAbility.kicker("{2}{R}"))
    keywords(Keyword.FIRST_STRIKE)

    // "Enters with … counters … and with haste" is a replacement
    // effect (rule 614.1c), not an ETB trigger — no stack, present the moment it enters.
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 2,
        selfOnly = true,
        condition = WasKicked
    ))
    replacementEffect(EntersWithKeywords(
        keywords = listOf(Keyword.HASTE),
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "158"
        artist = "Adam Rex"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e6e2e49-7bde-43c1-8caf-43d237dfc052.jpg?1562920517"
    }
}
