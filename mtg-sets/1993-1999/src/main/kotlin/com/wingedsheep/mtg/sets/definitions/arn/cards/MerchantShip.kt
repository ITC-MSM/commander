package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Merchant Ship
 * {U}
 * Creature — Human
 * 0/2
 * This creature can't attack unless defending player controls an Island.
 * Whenever this creature attacks and isn't blocked, you gain 2 life.
 * When you control no Islands, sacrifice this creature.
 */
val MerchantShip = card("Merchant Ship") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human"
    power = 0
    toughness = 2
    oracleText = "This creature can't attack unless defending player controls an Island.\n" +
        "Whenever this creature attacks and isn't blocked, you gain 2 life.\n" +
        "When you control no Islands, sacrifice this creature."

    staticAbility {
        ability = CantAttackUnless(Conditions.DefendingPlayerControlsLandType("Island"))
    }

    triggeredAbility {
        trigger = Triggers.AttacksAndIsntBlocked
        effect = Effects.GainLife(2, EffectTarget.Controller)
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
        rarity = Rarity.UNCOMMON
        collectorNumber = "17"
        artist = "Tom Wänerstrand"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b827094-fb2c-46db-b898-02e0c308601f.jpg?1562903045"
    }
}
