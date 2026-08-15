package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Akal Pakal, First Among Equals
 * {2}{U}
 * Legendary Creature — Human Advisor
 * 1/5
 * At the beginning of each player's end step, if an artifact entered the battlefield under your
 * control this turn, look at the top two cards of your library. Put one of them into your hand
 * and the other into your graveyard.
 *
 * The trigger fires at the beginning of EACH player's end step (not just your own), but the
 * intervening-if condition is always scoped to the source's controller ("under your control").
 * Rule 603.4 requires the condition to be true both at trigger time and at resolution.
 */
val AkalPakal = card("Akal Pakal, First Among Equals") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Advisor"
    power = 1
    toughness = 5
    oracleText = "At the beginning of each player's end step, if an artifact entered the battlefield under your control this turn, look at the top two cards of your library. Put one of them into your hand and the other into your graveyard."

    triggeredAbility {
        trigger = Triggers.EachEndStep
        triggerCondition = Conditions.ArtifactEnteredBattlefieldThisTurn
        effect = Patterns.Library.lookAtTopAndKeep(count = 2, keepCount = 1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "44"
        artist = "Ryan Pancoast"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab9f6a1b-8467-4584-affd-8c71d3e34d2f.jpg?1782694575"
    }
}
