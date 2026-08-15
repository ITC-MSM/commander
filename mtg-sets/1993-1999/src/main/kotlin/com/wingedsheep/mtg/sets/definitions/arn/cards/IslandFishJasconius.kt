package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Island Fish Jasconius
 * {4}{U}{U}{U}
 * Creature — Fish
 * 6/8
 * This creature doesn't untap during your untap step.
 * At the beginning of your upkeep, you may pay {U}{U}{U}. If you do, untap this creature.
 * This creature can't attack unless defending player controls an Island.
 * When you control no Islands, sacrifice this creature.
 */
val IslandFishJasconius = card("Island Fish Jasconius") {
    manaCost = "{4}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish"
    power = 6
    toughness = 8
    oracleText = "This creature doesn't untap during your untap step.\n" +
        "At the beginning of your upkeep, you may pay {U}{U}{U}. If you do, untap this creature.\n" +
        "This creature can't attack unless defending player controls an Island.\n" +
        "When you control no Islands, sacrifice this creature."

    flags(AbilityFlag.DOESNT_UNTAP)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = MayPayManaEffect(ManaCost.parse("{U}{U}{U}"), Effects.Untap(EffectTarget.Self))
    }

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
        rarity = Rarity.RARE
        collectorNumber = "16"
        artist = "Jesper Myrfors"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/8537cb0f-4821-417b-80cc-ea57d51ee9b8.jpg?1562919710"
    }
}
