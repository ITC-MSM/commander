package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Unburied Earthcarver — Tarkir: Dragonstorm #95
 * {1}{B} · Creature — Human Warrior · 2/2
 *
 * {2}, Sacrifice another creature: Put a +1/+1 counter on this creature.
 *
 * A repeatable sacrifice-outlet: the activation cost is {2} mana plus sacrificing another
 * creature ([Costs.SacrificeAnother]), and the effect adds a single +1/+1 counter to the
 * source ([Effects.AddCounters] targeting [EffectTarget.Self]). All existing primitives.
 */
val UnburiedEarthcarver = card("Unburied Earthcarver") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "{2}, Sacrifice another creature: Put a +1/+1 counter on this creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeAnother(GameObjectFilter.Creature))
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "{2}, Sacrifice another creature: Put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Inkognit"
        flavorText = "\"Unless you actually see my spirit join the Kin-Tree, never assume me so " +
            "unprofessional as to die on the job.\"\n—Ezcul of House Zanhar"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3ab5e71e-dc8d-4ed8-bcef-6497177c4a9d.jpg?1743204341"
    }
}
