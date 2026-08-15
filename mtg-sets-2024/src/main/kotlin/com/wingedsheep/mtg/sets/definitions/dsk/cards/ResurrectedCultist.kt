package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
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
 * Resurrected Cultist
 * {2}{B}
 * Creature — Human Cleric
 * 4/1
 * Delirium — {2}{B}{B}: Return this card from your graveyard to the battlefield with a finality
 * counter on it. Activate only if there are four or more card types among cards in your graveyard
 * and only as a sorcery. (If a creature with a finality counter on it would die, exile it instead.)
 */
val ResurrectedCultist = card("Resurrected Cultist") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Cleric"
    power = 4
    toughness = 1
    oracleText = "Delirium — {2}{B}{B}: Return this card from your graveyard to the battlefield with " +
        "a finality counter on it. Activate only if there are four or more card types among cards in " +
        "your graveyard and only as a sorcery. (If a creature with a finality counter on it would die, " +
        "exile it instead.)"

    // Delirium — {2}{B}{B}: Return this card from your graveyard to the battlefield with a finality
    // counter on it. Activate only if there are four or more card types among cards in your graveyard
    // and only as a sorcery.
    activatedAbility {
        cost = Costs.Mana("{2}{B}{B}")
        activateFromZone = Zone.GRAVEYARD
        timing = TimingRule.SorcerySpeed
        restrictions = listOf(ActivationRestriction.OnlyIfCondition(Conditions.Delirium()))
        effect = Effects.Composite(
            Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD),
            AddCountersEffect(counterType = Counters.FINALITY, count = 1, target = EffectTarget.Self),
        )
        description = "Delirium — {2}{B}{B}: Return this card from your graveyard to the battlefield " +
            "with a finality counter on it. Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Tyler Walpole"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e41bd259-e81f-432a-bebf-4c6534f23db7.jpg?1726286277"
    }
}
