package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val ProctorOfPotential = card("Proctor of Potential") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Human Cleric"
    power = 3
    toughness = 1
    oracleText = "Whenever this creature or another creature you control enters, surveil 1. (Look at " +
        "the top card of your library. You may put it into your graveyard.)\n" +
        "{W}{U}: Return this card from your graveyard to the battlefield with a finality counter on " +
        "it. Activate only if you've scried or surveilled this turn. (If a creature with a finality " +
        "counter on it would die, exile it instead.)"

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Surveil(1)
    }

    activatedAbility {
        cost = Costs.Mana("{W}{U}")
        activateFromZone = Zone.GRAVEYARD
        restrictions = listOf(ActivationRestriction.OnlyIfCondition(Conditions.ScriedOrSurveiledThisTurn))
        effect = Effects.Move(
            EffectTarget.Self,
            Zone.BATTLEFIELD,
            fromZone = Zone.GRAVEYARD,
            addCounterType = CounterType.FINALITY,
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "145"
        artist = "Andrey Kuzinskiy"
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf0eec8c-0475-4050-8144-481a9bb13a0f.jpg?1789127661"
        inBooster = false
    }
}
