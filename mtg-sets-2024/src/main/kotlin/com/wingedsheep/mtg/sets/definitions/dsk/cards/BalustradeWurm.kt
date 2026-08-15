package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Balustrade Wurm
 * {3}{G}{G}
 * Creature — Wurm
 * 5/5
 * This spell can't be countered.
 * Trample, haste
 * Delirium — {2}{G}{G}: Return this card from your graveyard to the battlefield with a finality
 * counter on it. Activate only if there are four or more card types among cards in your graveyard
 * and only as a sorcery.
 */
val BalustradeWurm = card("Balustrade Wurm") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wurm"
    power = 5
    toughness = 5
    oracleText = "This spell can't be countered.\nTrample, haste\nDelirium — {2}{G}{G}: Return this card from your graveyard to the battlefield with a finality counter on it. Activate only if there are four or more card types among cards in your graveyard and only as a sorcery."

    cantBeCountered = true
    keywords(Keyword.TRAMPLE, Keyword.HASTE)

    // Delirium — {2}{G}{G}: Return this card from your graveyard to the battlefield with a
    // finality counter on it. Activate only if there are four or more card types among cards in
    // your graveyard and only as a sorcery.
    activatedAbility {
        cost = Costs.Mana("{2}{G}{G}")
        activateFromZone = Zone.GRAVEYARD
        timing = TimingRule.SorcerySpeed
        restrictions = listOf(ActivationRestriction.OnlyIfCondition(Conditions.Delirium()))
        effect = Effects.Composite(
            Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD),
            AddCountersEffect(counterType = Counters.FINALITY, count = 1, target = EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "168"
        artist = "Maxime Minard"
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76610e6e-b60f-494f-bb11-68371eb494f2.jpg?1726286482"
    }
}
