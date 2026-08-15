package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Baloth Gorger
 * {2}{G}{G}
 * Creature — Beast
 * 4/4
 * Kicker {4}
 * If this creature was kicked, it enters with three +1/+1 counters on it.
 */
val BalothGorger = card("Baloth Gorger") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 4
    oracleText = "Kicker {4}\nIf this creature was kicked, it enters with three +1/+1 counters on it."

    keywordAbility(KeywordAbility.kicker("{4}"))

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 3,
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Zezhou Chen"
        flavorText = "A baloth only cares about the many things it eats and the few things that eat it."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/504090bb-d183-4833-aea5-d4193b5c57a1.jpg?1562735490"
    }
}
