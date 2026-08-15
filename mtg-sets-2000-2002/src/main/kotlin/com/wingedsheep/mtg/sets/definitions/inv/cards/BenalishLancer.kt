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
 * Benalish Lancer
 * {2}{W}
 * Creature — Human Knight
 * 2/2
 * Kicker {2}{W}
 * If this creature was kicked, it enters with two +1/+1 counters on it and with first strike.
 */
val BenalishLancer = card("Benalish Lancer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 2
    oracleText = "Kicker {2}{W} (You may pay an additional {2}{W} as you cast this spell.)\n" +
        "If this creature was kicked, it enters with two +1/+1 counters on it and with first strike."

    keywordAbility(KeywordAbility.kicker("{2}{W}"))

    // "Enters with … counters … and with first strike" is a replacement
    // effect (rule 614.1c), not an ETB trigger — no stack, present the moment it enters.
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 2,
        selfOnly = true,
        condition = WasKicked
    ))
    replacementEffect(EntersWithKeywords(
        keywords = listOf(Keyword.FIRST_STRIKE),
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a38d40a-e745-4fee-b179-f8c27e9b2fbd.jpg?1562906778"
    }
}
