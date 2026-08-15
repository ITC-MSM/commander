package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sorceress Queen
 * {1}{B}{B}
 * Creature — Human Wizard Sorcerer
 * 1/1
 * {T}: Target creature other than this creature has base power and toughness 0/2 until end of turn.
 */
val SorceressQueen = card("Sorceress Queen") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard Sorcerer"
    power = 1
    toughness = 1
    oracleText = "{T}: Target creature other than this creature has base power and toughness 0/2 until end of turn."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature other than this creature", TargetObject(filter = TargetFilter.OtherCreature))
        effect = Effects.SetBasePowerAndToughness(0, 2, creature, Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "32"
        artist = "Kaja Foglio"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94742003-f0f1-4483-b1a0-e7163995db1b.jpg?1562922545"
    }
}
