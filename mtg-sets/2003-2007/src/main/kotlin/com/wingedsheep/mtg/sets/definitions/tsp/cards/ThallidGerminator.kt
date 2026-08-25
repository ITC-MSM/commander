package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thallid Germinator
 * {2}{G}
 * Creature — Fungus
 * 2 / 2
 * At the beginning of your upkeep, put a spore counter on this creature.
 * Remove three spore counters from this creature: Create a 1/1 green Saproling creature token.
 * Sacrifice a Saproling: Target creature gets +1/+1 until end of turn.
 */
val ThallidGerminator = card("Thallid Germinator") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus"
    power = 2
    toughness = 2
    oracleText = "At the beginning of your upkeep, put a spore counter on this creature.\n" +
        "Remove three spore counters from this creature: Create a 1/1 green Saproling creature token.\n" +
        "Sacrifice a Saproling: Target creature gets +1/+1 until end of turn."

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
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, t)
        description = "Sacrifice a Saproling: Target creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "225"
        artist = "Tom Wänerstrand"
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac526fb0-e6d0-4ee0-9888-15098c1704df.jpg"
    }
}
