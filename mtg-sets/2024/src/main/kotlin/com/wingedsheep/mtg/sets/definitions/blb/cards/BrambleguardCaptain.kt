package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Brambleguard Captain
 * {3}{R}
 * Creature — Mouse Soldier
 * 2/3
 *
 * At the beginning of combat on your turn, target creature you control
 * gets +X/+0 until end of turn, where X is this creature's power.
 */
val BrambleguardCaptain = card("Brambleguard Captain") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Mouse Soldier"
    power = 2
    toughness = 3
    oracleText = "At the beginning of combat on your turn, target creature you control gets +X/+0 until end of turn, where X is this creature's power."

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val t = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.ModifyStats(
            power = DynamicAmount.EntityProperty(EntityReference.Source, EntityNumericProperty.Power),
            toughness = DynamicAmount.Fixed(0),
            target = t
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "127"
        artist = "Quintin Gleim"
        flavorText = "\"We sought adventure and found calamity. Forward, so we may meet both with courage!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e200b8bf-f2f3-4157-8e04-02baf07a963e.jpg?1721426582"
    }
}
