package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val FrostbitePyromental = card("Frostbite Pyromental") {
    manaCost = "{U}{R}{R}"
    colorIdentity = "RU"
    typeLine = "Creature — Elemental"
    oracleText = "Trample, haste\nWhenever this creature deals combat damage to a player, draw two cards.\nAt the beginning of the end step, sacrifice this creature."
    power = 4
    toughness = 4

    keywords(Keyword.TRAMPLE, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.DrawCards(2)
    }

    triggeredAbility {
        trigger = Triggers.EachEndStep
        effect = Effects.SacrificeTarget(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "135"
        artist = "Wayne Reynolds"
        flavorText = "It's a contradiction in burns."
        imageUri = "https://cards.scryfall.io/normal/front/7/a/7a44581f-8fc4-457d-888a-1e211090ee7e.jpg?1789470870"
        inBooster = false
    }
}
