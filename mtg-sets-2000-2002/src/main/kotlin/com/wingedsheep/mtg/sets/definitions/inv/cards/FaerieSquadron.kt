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
 * Faerie Squadron
 * {U}
 * Creature — Faerie
 * 1/1
 * Kicker {3}{U} (You may pay an additional {3}{U} as you cast this spell.)
 * If this creature was kicked, it enters with two +1/+1 counters on it and with flying.
 */
val FaerieSquadron = card("Faerie Squadron") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie"
    power = 1
    toughness = 1
    oracleText = "Kicker {3}{U} (You may pay an additional {3}{U} as you cast this spell.)\n" +
        "If this creature was kicked, it enters with two +1/+1 counters on it and with flying."

    keywordAbility(KeywordAbility.kicker("{3}{U}"))

    // "Enters with … counters … and with flying" is a replacement
    // effect (rule 614.1c), not an ETB trigger — no stack, present the moment it enters.
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 2,
        selfOnly = true,
        condition = WasKicked
    ))
    replacementEffect(EntersWithKeywords(
        keywords = listOf(Keyword.FLYING),
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "rk post"
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c707c81-dbbd-43be-a79a-7bc92a584839.jpg?1562910474"
    }
}
