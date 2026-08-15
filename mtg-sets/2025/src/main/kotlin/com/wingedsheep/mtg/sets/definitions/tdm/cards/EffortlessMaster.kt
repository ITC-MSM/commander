package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Effortless Master
 * {2}{U}{R}
 * Creature — Orc Monk
 * 4/3
 *
 * Vigilance
 * Menace
 * This creature enters with two +1/+1 counters on it if you've cast two or more spells this turn.
 */
val EffortlessMaster = card("Effortless Master") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Creature — Orc Monk"
    power = 4
    toughness = 3
    oracleText = "Vigilance\nMenace\n" +
        "This creature enters with two +1/+1 counters on it if you've cast two or more spells this turn."

    keywords(Keyword.VIGILANCE, Keyword.MENACE)

    replacementEffect(EntersWithCounters(
        counterType = CounterTypeFilter.PlusOnePlusOne,
        count = 2,
        selfOnly = true,
        condition = Conditions.YouCastSpellsThisTurn(atLeast = 2)
    ))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "181"
        artist = "Lie Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0ae03ca5-cd4b-42b7-8cd5-3f7e753b4147.jpg?1743204705"
    }
}
