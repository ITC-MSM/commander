package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Savage Thallid
 * {3}{G}{G}
 * Creature — Fungus
 * 5 / 2
 * At the beginning of your upkeep, put a spore counter on this creature.
 * Remove three spore counters from this creature: Create a 1/1 green Saproling creature token.
 * Sacrifice a Saproling: Regenerate target Fungus.
 *
 * "Target Fungus" is a permanent-position subtype filter, not a creature one — a Fungus that
 * somehow isn't a creature is still a legal target. There is no `Effects.Regenerate` facade;
 * [RegenerateEffect] is the shipped spelling (Reknit, Crypt Sliver).
 */
val SavageThallid = card("Savage Thallid") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Fungus"
    power = 5
    toughness = 2
    oracleText = "At the beginning of your upkeep, put a spore counter on this creature.\n" +
        "Remove three spore counters from this creature: Create a 1/1 green Saproling creature token.\n" +
        "Sacrifice a Saproling: Regenerate target Fungus."

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
        val t = target(
            "target",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Permanent.withSubtype(Subtype.FUNGUS)))
        )
        effect = RegenerateEffect(t)
        description = "Sacrifice a Saproling: Regenerate target Fungus."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "213"
        artist = "Luca Zontini"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a87cbbdb-3bbc-48da-b5e3-fcdbddebec81.jpg"
    }
}
