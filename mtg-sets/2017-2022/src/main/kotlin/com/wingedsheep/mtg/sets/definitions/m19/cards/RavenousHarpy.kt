package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ravenous Harpy
 * {2}{B}
 * Creature — Harpy
 * 1/2
 * Flying
 * {1}, Sacrifice another creature: Put a +1/+1 counter on this creature.
 */
val RavenousHarpy = card("Ravenous Harpy") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Harpy"
    power = 1
    toughness = 2
    oracleText = "Flying\n" +
        "{1}, Sacrifice another creature: Put a +1/+1 counter on this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.SacrificeAnother(GameObjectFilter.Creature)
        )
        effect = Effects.AddCounters("+1/+1", 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "115"
        artist = "Sam Rowan"
        flavorText = "A harpy's hoard is a filthy, bloodstained pile of trinkets and corpses."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/676ec702-75c4-4733-b500-eb15406778bb.jpg"
    }
}
