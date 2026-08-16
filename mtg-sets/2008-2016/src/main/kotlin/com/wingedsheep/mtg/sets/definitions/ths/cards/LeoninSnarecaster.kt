package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Leonin Snarecaster
 * {1}{W}
 * Creature — Cat Soldier
 * 2/1
 * When this creature enters, you may tap target creature.
 */
val LeoninSnarecaster = card("Leonin Snarecaster") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Soldier"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, you may tap target creature."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Kev Walker"
        flavorText = "Formerly oppressed by the polis of Meletis, leonin occasionally \"mistake\" their old enemies for game."
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ade9b739-122c-4659-b1b2-ea0510d96dbc.jpg?1783939812"
    }
}
