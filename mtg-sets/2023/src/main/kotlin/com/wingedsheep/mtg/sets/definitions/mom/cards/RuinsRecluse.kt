package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ruins Recluse
 * {1}{G}
 * Creature — Spider
 * 1/1
 * Reach, deathtouch
 * {3}{G}: Put a +1/+1 counter on this creature.
 */
val RuinsRecluse = card("Ruins Recluse") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    oracleText = "Reach, deathtouch\n{3}{G}: Put a +1/+1 counter on this creature."
    power = 1
    toughness = 1

    keywords(Keyword.REACH, Keyword.DEATHTOUCH)

    activatedAbility {
        cost = Costs.Mana("{3}{G}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "336"
        artist = "Lorenzo Mastroianni"
        flavorText = "Desert horrors were its favorite treat, but it happily expanded its diet to " +
            "include extraplanar horrors."
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c0c0fdf5-2f56-4480-9ccb-84471b3e5331.jpg?1783916899"
    }
}
