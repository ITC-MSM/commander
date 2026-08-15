package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Ardent Soldier
 * {1}{W}
 * Creature — Human Soldier
 * 1/2
 * Kicker {2}
 * Vigilance
 * If this creature was kicked, it enters with a +1/+1 counter on it.
 */
val ArdentSoldier = card("Ardent Soldier") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 2
    oracleText = "Kicker {2} (You may pay an additional {2} as you cast this spell.)\n" +
        "Vigilance\n" +
        "If this creature was kicked, it enters with a +1/+1 counter on it."

    keywords(Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.kicker("{2}"))

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/39dce974-846f-4365-b0a5-851e38668e7d.jpg?1562906683"
    }
}
