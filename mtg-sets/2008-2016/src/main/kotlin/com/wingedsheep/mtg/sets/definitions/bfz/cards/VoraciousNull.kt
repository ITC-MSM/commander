package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Voracious Null
 * {2}{B}
 * Creature — Zombie
 * 2/2
 * {1}{B}, Sacrifice another creature: Put two +1/+1 counters on this creature. Activate only as a sorcery.
 */
val VoraciousNull = card("Voracious Null") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 2
    oracleText = "{1}{B}, Sacrifice another creature: Put two +1/+1 counters on this creature. Activate only as a " +
        "sorcery."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{B}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature),
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "125"
        artist = "Karl Kopinski"
        flavorText = "\"These days, there's no shortage of food for the nulls of Guul Draz.\"\n" +
            "—Drana, Kalastria bloodchief"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74364056-f4ee-46d3-834d-a5157ec312b9.jpg?1783938198"
    }
}
