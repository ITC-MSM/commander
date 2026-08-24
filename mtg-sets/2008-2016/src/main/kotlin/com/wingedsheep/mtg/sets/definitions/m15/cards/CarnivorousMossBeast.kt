package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Carnivorous Moss-Beast
 * {4}{G}{G}
 * Creature — Plant Elemental Beast
 * 4/5
 * {5}{G}{G}: Put a +1/+1 counter on this creature.
 */
val CarnivorousMossBeast = card("Carnivorous Moss-Beast") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Elemental Beast"
    power = 4
    toughness = 5
    oracleText = "{5}{G}{G}: Put a +1/+1 counter on this creature."

    activatedAbility {
        cost = Costs.Mana("{5}{G}{G}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Filip Burburan"
        flavorText = "Ranger wisdom dictates that when fleeing from a moss-beast, you must stay calm, find your bearings, and run south."
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd814ce3-9555-4e9d-a212-e40717f4e546.jpg?1783939168"
    }
}
