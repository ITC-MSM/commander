package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Novice Dissector — Strixhaven: School of Mages #79 (canonical printing)
 * {3}{B} · Creature — Troll Warlock · 3/3
 *
 * {1}, Sacrifice another creature: Put a +1/+1 counter on target creature. Activate only as a sorcery.
 *
 * The Gobbling Ooze cost with a targeted payoff: [Costs.Composite] of the mana and
 * [Costs.SacrificeAnother] — [Costs.Sacrifice] with the source excluded, so the Dissector can never
 * feed itself to the ability — then [Effects.AddCounters] of one +1/+1 counter on any target
 * creature. "Activate only as a sorcery" is [TimingRule.SorcerySpeed].
 */
val NoviceDissector = card("Novice Dissector") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Troll Warlock"
    oracleText =
        "{1}, Sacrifice another creature: Put a +1/+1 counter on target creature. Activate only as a sorcery."
    power = 3
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature),
        )
        val creature = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
        timing = TimingRule.SorcerySpeed
        description = "{1}, Sacrifice another creature: Put a +1/+1 counter on target creature. " +
            "Activate only as a sorcery."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "79"
        artist = "Mads Ahm"
        flavorText = "\"The professor said to first extract the venom glands, then the acid sac. Next up, the . . . meeping organ?\""
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c4677bc-736b-4bf2-851e-b158718a4224.jpg?1783927363"
    }
}
