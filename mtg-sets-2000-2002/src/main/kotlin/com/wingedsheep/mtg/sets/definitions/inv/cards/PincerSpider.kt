package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Pincer Spider
 * {2}{G}
 * Creature — Spider
 * 2/3
 * Kicker {3}
 * Reach
 * If this creature was kicked, it enters with a +1/+1 counter on it.
 */
val PincerSpider = card("Pincer Spider") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    power = 2
    toughness = 3
    oracleText = "Kicker {3} (You may pay an additional {3} as you cast this spell.)\n" +
        "Reach (This creature can block creatures with flying.)\n" +
        "If this creature was kicked, it enters with a +1/+1 counter on it."

    keywords(Keyword.REACH)
    keywordAbility(KeywordAbility.kicker("{3}"))

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "201"
        artist = "Dan Frazier"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/23271658-19ae-420d-beeb-4bed4fdbb891.jpg?1562902046"
    }
}
