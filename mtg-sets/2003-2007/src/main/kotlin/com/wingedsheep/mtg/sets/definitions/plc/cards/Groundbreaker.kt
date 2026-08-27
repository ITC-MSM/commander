package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Groundbreaker
 * {G}{G}{G}
 * Creature — Elemental
 * 6/1
 * Trample, haste
 * At the beginning of the end step, sacrifice this creature.
 *
 * "The end step", not "your end step" — [Triggers.EachEndStep], so a Groundbreaker that changed
 * controllers or entered on an opponent's turn is still sacrificed that turn.
 */
val Groundbreaker = card("Groundbreaker") {
    manaCost = "{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    power = 6
    toughness = 1
    oracleText = "Trample, haste\n" +
        "At the beginning of the end step, sacrifice this creature."

    keywords(Keyword.TRAMPLE, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EachEndStep
        effect = SacrificeSelfEffect
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "148"
        artist = "Matt Cavotta"
        flavorText = "The earth's memory is long, its retribution brief."
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f467cdea-6166-4289-918b-18f5038c94ed.jpg"
    }
}
