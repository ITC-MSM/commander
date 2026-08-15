package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Beastie Beatdown
 * {R}{G}
 * Sorcery
 * Choose target creature you control and target creature an opponent controls.
 * Delirium — If there are four or more card types among cards in your graveyard, put two
 * +1/+1 counters on the creature you control.
 * The creature you control deals damage equal to its power to the creature an opponent controls.
 *
 * One-sided "fight": only the controlled creature deals damage. The Delirium counters are placed
 * before the damage step so the (possibly buffed) power is used. `targetPower(0)` reads the first
 * declared target (the controlled creature, also the damage source).
 */
val BeastieBeatdown = card("Beastie Beatdown") {
    manaCost = "{R}{G}"
    colorIdentity = "RG"
    typeLine = "Sorcery"
    oracleText = "Choose target creature you control and target creature an opponent controls.\n" +
        "Delirium — If there are four or more card types among cards in your graveyard, put two " +
        "+1/+1 counters on the creature you control.\n" +
        "The creature you control deals damage equal to its power to the creature an opponent controls."

    spell {
        val yours = target("creature you control", TargetCreature(filter = TargetFilter.Creature.youControl()))
        val theirs = target("creature an opponent controls", TargetCreature(filter = TargetFilter.Creature.opponentControls()))
        effect = Effects.Composite(
            // Delirium — counters land first so the damage uses the buffed power.
            ConditionalEffect(
                condition = Conditions.Delirium(),
                effect = AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 2, target = yours),
            ),
            DealDamageEffect(DynamicAmounts.targetPower(0), theirs, damageSource = yours),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "210"
        artist = "Inkognit"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f889c95-46af-4fd9-aff2-573d5384fd58.jpg?1726286652"
    }
}
