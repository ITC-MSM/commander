package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thallid Shell-Dweller
 * {1}{G}
 * Creature — Fungus
 * 0 / 5
 * Defender
 * At the beginning of your upkeep, put a spore counter on this creature.
 * Remove three spore counters from this creature: Create a 1/1 green Saproling creature token.
 */
val ThallidShellDweller = card("Thallid Shell-Dweller") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus"
    power = 0
    toughness = 5
    oracleText = "Defender\n" +
        "At the beginning of your upkeep, put a spore counter on this creature.\n" +
        "Remove three spore counters from this creature: Create a 1/1 green Saproling creature token."

    keywords(Keyword.DEFENDER)

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

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "226"
        artist = "Carl Critchlow"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4ee36256-022f-49b3-8914-9b1c2f4dc506.jpg"
    }
}
