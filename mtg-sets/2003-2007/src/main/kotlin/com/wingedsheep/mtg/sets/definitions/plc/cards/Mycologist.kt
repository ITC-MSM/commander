package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mycologist
 * {1}{W}
 * Creature — Human Druid
 * 0/2
 * At the beginning of your upkeep, put a spore counter on this creature.
 * Remove three spore counters from this creature: Create a 1/1 green Saproling creature token.
 * Sacrifice a Saproling: You gain 2 life.
 *
 * Planar Chaos's white Thallid. "Sacrifice a Saproling" is the bare tribal noun — any Saproling
 * *permanent*, which in practice is the token this card makes.
 */
val Mycologist = card("Mycologist") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Druid"
    power = 0
    toughness = 2
    oracleText = "At the beginning of your upkeep, put a spore counter on this creature.\n" +
        "Remove three spore counters from this creature: Create a 1/1 green Saproling creature token.\n" +
        "Sacrifice a Saproling: You gain 2 life."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.AddCounters(Counters.SPORE, 1, EffectTarget.Self)
        description = "At the beginning of your upkeep, put a spore counter on this creature."
    }

    activatedAbility {
        cost = Costs.RemoveCounterFromSelf(Counters.SPORE, 3)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling")
        )
        description = "Remove three spore counters from this creature: Create a 1/1 green Saproling creature token."
    }

    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.SAPROLING))
        effect = Effects.GainLife(2)
        description = "Sacrifice a Saproling: You gain 2 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "27"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56d8e9fa-e241-4a11-99b8-ffced81eb38b.jpg"
    }
}
