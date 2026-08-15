package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Stronghold Confessor
 * {B}
 * Creature — Human Cleric
 * 1/1
 * Kicker {3}
 * Menace
 * If this creature was kicked, it enters with two +1/+1 counters on it.
 */
val StrongholdConfessor = card("Stronghold Confessor") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Cleric"
    power = 1
    toughness = 1
    oracleText = "Kicker {3}\nMenace\nIf this creature was kicked, it enters with two +1/+1 counters on it."

    keywordAbility(KeywordAbility.kicker("{3}"))
    keywords(Keyword.MENACE)

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 2,
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "105"
        artist = "Igor Kieryluk"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab3fcc43-839b-48c1-91e3-7cc80d8c7f9a.jpg?1562741080"
    }
}
