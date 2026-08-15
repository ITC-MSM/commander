package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Academy Drake
 * {2}{U}
 * Creature — Drake
 * 2/2
 * Kicker {4}
 * Flying
 * If this creature was kicked, it enters with two +1/+1 counters on it.
 */
val AcademyDrake = card("Academy Drake") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    power = 2
    toughness = 2
    oracleText = "Kicker {4}\nFlying\nIf this creature was kicked, it enters with two +1/+1 counters on it."

    keywordAbility(KeywordAbility.kicker("{4}"))
    keywords(Keyword.FLYING)

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 2,
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8bacb12-da46-4b00-804f-9ff6bff452bc.jpg?1562745962"
    }
}
