package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Liliana the Repentant — the exhaust ability's only target is the graveyard card, so if that card
 * has left the graveyard by resolution the whole ability doesn't resolve and Liliana gets no counter.
 */
val LilianaTheRepentant = card("Liliana the Repentant") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Warlock"
    oracleText = "Whenever another creature or planeswalker you control enters, mill two cards.\n" +
        "Exhaust — {5}{B}: Return target creature or planeswalker card from your graveyard to the battlefield. " +
        "Put a +1/+1 counter on Liliana. Activate only as a sorcery. (Activate each exhaust ability only once.)"
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.CreatureOrPlaneswalker.youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = Patterns.Library.mill(2)
        description = "Whenever another creature or planeswalker you control enters, mill two cards."
    }

    activatedAbility {
        cost = Costs.Mana("{5}{B}")
        isExhaust = true
        timing = TimingRule.SorcerySpeed
        val card = target(
            "target creature or planeswalker card from your graveyard",
            TargetObject(
                filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.ownedByYou(), zone = Zone.GRAVEYARD)
            ),
        )
        effect = Effects.Composite(
            Effects.Move(card, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
        )
        description = "Return target creature or planeswalker card from your graveyard to the battlefield. " +
            "Put a +1/+1 counter on Liliana."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "231"
        artist = "Marta Nael"
        flavorText = "\"My mistakes are *mine* to fix.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1eb25a6c-d6b4-465d-990e-f1ab86b26b69.jpg?1788329273"
        inBooster = false
    }
}
