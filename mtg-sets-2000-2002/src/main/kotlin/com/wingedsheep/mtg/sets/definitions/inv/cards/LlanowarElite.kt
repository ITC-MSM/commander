package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Llanowar Elite
 * {G}
 * Creature — Elf
 * 1/1
 * Kicker {8}
 * Trample
 * If this creature was kicked, it enters with five +1/+1 counters on it.
 */
val LlanowarElite = card("Llanowar Elite") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf"
    power = 1
    toughness = 1
    oracleText = "Kicker {8} (You may pay an additional {8} as you cast this spell.)\n" +
        "Trample\n" +
        "If this creature was kicked, it enters with five +1/+1 counters on it."

    keywordAbility(KeywordAbility.kicker("{8}"))
    keywords(Keyword.TRAMPLE)

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 5,
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "196"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e207863-de68-47e1-8c63-413b5fa48943.jpg?1562907508"
    }
}
