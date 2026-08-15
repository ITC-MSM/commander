package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Vodalian Serpent
 * {3}{U}
 * Creature — Serpent
 * 2/2
 * Kicker {2} (You may pay an additional {2} as you cast this spell.)
 * This creature can't attack unless defending player controls an Island.
 * If this creature was kicked, it enters with four +1/+1 counters on it.
 */
val VodalianSerpent = card("Vodalian Serpent") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Serpent"
    power = 2
    toughness = 2
    oracleText = "Kicker {2} (You may pay an additional {2} as you cast this spell.)\n" +
        "This creature can't attack unless defending player controls an Island.\n" +
        "If this creature was kicked, it enters with four +1/+1 counters on it."

    keywordAbility(KeywordAbility.kicker("{2}"))

    staticAbility {
        ability = CantAttackUnless(Conditions.DefendingPlayerControlsLandType("Island"))
    }

    // "Enters with a counter" is a replacement effect (rule 614.1c), not an ETB trigger
    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 4,
        selfOnly = true,
        condition = WasKicked
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "86"
        artist = "Christopher Moeller"
        imageUri = "https://cards.scryfall.io/normal/front/9/2/92adcf6c-ab14-414c-a5cb-56feae048c84.jpg?1562924617"
    }
}
