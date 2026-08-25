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
 * Deathspore Thallid
 * {1}{B}
 * Creature — Zombie Fungus
 * 1 / 1
 * At the beginning of your upkeep, put a spore counter on this creature.
 * Remove three spore counters from this creature: Create a 1/1 green Saproling creature token.
 * Sacrifice a Saproling: Target creature gets -1/-1 until end of turn.
 */
val DeathsporeThallid = card("Deathspore Thallid") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Fungus"
    power = 1
    toughness = 1
    oracleText = "At the beginning of your upkeep, put a spore counter on this creature.\n" +
        "Remove three spore counters from this creature: Create a 1/1 green Saproling creature token.\n" +
        "Sacrifice a Saproling: Target creature gets -1/-1 until end of turn."

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
        effect = Effects.ModifyStats(-1, -1, t)
        description = "Sacrifice a Saproling: Target creature gets -1/-1 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "102"
        artist = "Randy Elliott"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44ee5ee3-11f8-4a4e-bfa3-10ff45ed6d1b.jpg"
    }
}
