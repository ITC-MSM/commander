package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Throw from the Saddle
 * {1}{G}
 * Sorcery
 *
 * Target creature you control gets +1/+1 until end of turn. Put a +1/+1 counter on it instead
 * if it's a Mount. Then it deals damage equal to its power to target creature you don't control.
 *
 * The boost branches on the Mount test for target 0: a Mount gets a permanent +1/+1 counter,
 * otherwise a temporary +1/+1. The boost resolves first so the subsequent damage uses the
 * already-boosted power. Damage is sourced from target 0 (so it triggers "deals damage" effects on
 * that creature) and dealt to target 1.
 */
val ThrowFromTheSaddle = card("Throw from the Saddle") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Target creature you control gets +1/+1 until end of turn. Put a +1/+1 counter " +
        "on it instead if it's a Mount. Then it deals damage equal to its power to target " +
        "creature you don't control."

    spell {
        val mine = target("creature you control", Targets.CreatureYouControl)
        val theirs = target("creature you don't control", Targets.CreatureOpponentControls)

        val boost = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(
                GameObjectFilter.Creature.withSubtype(Subtype("Mount")), targetIndex = 0
            ),
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, mine),
            elseEffect = Effects.ModifyStats(1, 1, mine)
        )

        val damage = DealDamageEffect(
            amount = DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.Power),
            target = theirs,
            damageSource = mine
        )

        effect = boost.then(damage)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "Eilene Cherie"
        flavorText = "\"A little kindness would've carried you a lot farther than those spurs.\"\n—Miriam, herd whisperer"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/775874cc-4b78-4904-9c97-431c2e400c64.jpg?1712356011"
    }
}
