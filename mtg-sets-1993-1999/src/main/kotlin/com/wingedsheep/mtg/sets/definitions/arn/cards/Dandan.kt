package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dandân
 * {U}{U}
 * Creature — Fish
 * 4/1
 * This creature can't attack unless defending player controls an Island.
 * When you control no Islands, sacrifice this creature.
 */
val Dandan = card("Dandân") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish"
    power = 4
    toughness = 1
    oracleText = "This creature can't attack unless defending player controls an Island.\n" +
        "When you control no Islands, sacrifice this creature."

    staticAbility {
        ability = CantAttackUnless(Conditions.DefendingPlayerControlsLandType("Island"))
    }

    stateTriggeredAbility {
        condition = Conditions.YouControl(
            GameObjectFilter.Land.withSubtype("Island"),
            negate = true
        )
        effect = Effects.SacrificeTarget(EffectTarget.Self)
        description = "When you control no Islands, sacrifice this creature"
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Drew Tucker"
        imageUri = "https://cards.scryfall.io/normal/front/4/1/414d3cae-b8cf-4d53-bd6b-1aa83a828ba9.jpg?1562906979"
    }
}
