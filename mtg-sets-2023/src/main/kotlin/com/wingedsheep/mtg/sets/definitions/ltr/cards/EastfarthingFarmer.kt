package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Eastfarthing Farmer
 * {2}{W}
 * Creature — Halfling Peasant
 * 2/3
 * When this creature enters, create a Food token. When you do, target creature you control
 * gets +1/+1 until end of turn for each Food you control.
 */
val EastfarthingFarmer = card("Eastfarthing Farmer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Halfling Peasant"
    power = 2
    toughness = 3
    oracleText = "When this creature enters, create a Food token. When you do, target creature you control gets +1/+1 until end of turn for each Food you control. (A Food token is an artifact with \"{2}, {T}, Sacrifice this token: You gain 3 life.\")"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = Effects.CreateFood(),
            optional = false,
            reflexiveEffect = Effects.ModifyStats(
                DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Any.withSubtype("Food")),
                DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Any.withSubtype("Food")),
                EffectTarget.ContextTarget(0)
            ),
            reflexiveTargetRequirements = listOf(
                TargetCreature(filter = TargetFilter.CreatureYouControl)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Iga Oliwiak"
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26bb65ba-d605-4b79-a700-4a08ec5a90b4.jpg?1686967706"
    }
}
