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
 * Duskwalker
 * {B}
 * Creature — Human Minion
 * 1/1
 * Kicker {3}{B}
 * If this creature was kicked, it enters with two +1/+1 counters on it and with fear.
 */
val Duskwalker = card("Duskwalker") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Minion"
    power = 1
    toughness = 1
    oracleText = "Kicker {3}{B} (You may pay an additional {3}{B} as you cast this spell.)\n" +
        "If this creature was kicked, it enters with two +1/+1 counters on it and with fear. " +
        "(It can't be blocked except by artifact creatures and/or black creatures.)"

    keywordAbility(KeywordAbility.kicker("{3}{B}"))

    // "Enters with … counters … and with fear" is a replacement
    // effect (rule 614.1c), not an ETB trigger — no stack, present the moment it enters.
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 2,
        selfOnly = true,
        condition = WasKicked
    ))
    replacementEffect(EntersWithKeywords(
        keywords = listOf(Keyword.FEAR),
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "David Martin"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39a4a026-f44e-40e1-9942-a3d8448aca70.jpg?1562906613"
    }
}
