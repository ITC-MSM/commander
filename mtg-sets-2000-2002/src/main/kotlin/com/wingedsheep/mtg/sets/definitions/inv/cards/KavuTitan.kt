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
 * Kavu Titan
 * {1}{G}
 * Creature — Kavu
 * 2/2
 * Kicker {2}{G}
 * If this creature was kicked, it enters with three +1/+1 counters on it and with trample.
 */
val KavuTitan = card("Kavu Titan") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Kavu"
    power = 2
    toughness = 2
    oracleText = "Kicker {2}{G} (You may pay an additional {2}{G} as you cast this spell.)\n" +
        "If this creature was kicked, it enters with three +1/+1 counters on it and with trample."

    keywordAbility(KeywordAbility.kicker("{2}{G}"))

    // "Enters with … counters … and with trample" is a replacement effect (rule 614.1c),
    // not an ETB trigger: a kicked Titan is a 5/5 trampler from the moment it enters.
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 3,
        selfOnly = true,
        condition = WasKicked
    ))
    replacementEffect(EntersWithKeywords(
        keywords = listOf(Keyword.TRAMPLE),
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "194"
        artist = "Todd Lockwood"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c5fb86d-1d9a-4da2-bb5b-4266faa20197.jpg?1562904050"
    }
}
