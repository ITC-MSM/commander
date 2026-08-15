package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Skizzik
 * {3}{R}
 * Creature — Elemental
 * 5/3
 * Kicker {R}
 * Trample, haste
 * At the beginning of the end step, if this creature wasn't kicked, sacrifice it.
 */
val Skizzik = card("Skizzik") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 5
    toughness = 3
    oracleText = "Kicker {R} (You may pay an additional {R} as you cast this spell.)\n" +
        "Trample, haste\n" +
        "At the beginning of the end step, if this creature wasn't kicked, sacrifice it."

    keywords(Keyword.TRAMPLE, Keyword.HASTE)
    keywordAbility(KeywordAbility.kicker("{R}"))

    triggeredAbility {
        trigger = Triggers.EachEndStep
        triggerCondition = Conditions.Not(Conditions.WasKicked)
        effect = SacrificeSelfEffect
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "169"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dc7732bc-e168-44d9-923a-db7e985bd6db.jpg?1562939314"
    }
}
