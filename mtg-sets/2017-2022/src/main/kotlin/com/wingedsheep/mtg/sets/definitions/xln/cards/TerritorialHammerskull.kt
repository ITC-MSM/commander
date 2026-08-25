package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Territorial Hammerskull
 * {2}{W}
 * Creature — Dinosaur
 * 2/3
 *
 * Whenever this creature attacks, tap target creature an opponent controls.
 */
val TerritorialHammerskull = card("Territorial Hammerskull") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dinosaur"
    oracleText = "Whenever this creature attacks, tap target creature an opponent controls."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.Attacks
        val victim = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.opponentControls()))
        )
        effect = Effects.Tap(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Lars Grant-West"
        flavorText = "From the eyes up, it's solid bone and stubbornness."
        imageUri = "https://cards.scryfall.io/normal/front/a/f/af5a237a-31e7-43ee-8d47-3eb12dd1a60c.jpg"
    }
}
