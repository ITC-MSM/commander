package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Goblin Boarders
 * {2}{R}
 * Creature — Goblin Pirate
 * 3/2
 *
 * Raid — This creature enters with a +1/+1 counter on it if you attacked this turn.
 *
 * "Enters with a counter" is a replacement effect (rule 614.1c), not a trigger:
 * it never uses the stack and the creature is a 4/3 from the moment it enters
 * (Frilled Sparkshooter follows the same pattern).
 */
val GoblinBoarders = card("Goblin Boarders") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Pirate"
    power = 3
    toughness = 2
    oracleText = "Raid — This creature enters with a +1/+1 counter on it if you attacked this turn."

    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 1,
        selfOnly = true,
        condition = Conditions.YouAttackedThisTurn
    ))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Filipe Pagliuso"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/4409a063-bf2a-4a49-803e-3ce6bd474353.jpg?1782689191"
    }
}
