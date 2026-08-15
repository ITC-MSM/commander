package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Untamed Kavu
 * {1}{G}
 * Creature — Kavu
 * 2/2
 * Kicker {3}
 * Vigilance, trample
 * If this creature was kicked, it enters with three +1/+1 counters on it.
 */
val UntamedKavu = card("Untamed Kavu") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Kavu"
    power = 2
    toughness = 2
    oracleText = "Kicker {3}\nVigilance, trample\nIf this creature was kicked, it enters with three +1/+1 counters on it."

    keywordAbility(KeywordAbility.kicker("{3}"))
    keywords(Keyword.VIGILANCE, Keyword.TRAMPLE)

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 3,
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "186"
        artist = "Yongjae Choi"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c4cf84a-2024-45bb-9e24-8a9a6d9ad247.jpg?1562734310"
    }
}
