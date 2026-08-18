package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Diabolic Machine
 * {7}
 * Artifact Creature — Construct
 * 4/4
 * {3}: Regenerate this creature.
 */
val DiabolicMachine = card("Diabolic Machine") {
    manaCost = "{7}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    power = 4
    toughness = 4
    oracleText = "{3}: Regenerate this creature."

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "101"
        artist = "Anson Maddocks"
        flavorText = "\"The bolts of our ballistae smashed into the monstrous thing, but our hopes died in our chests as its gears continued turning.\" —Sevti Mukul, *The Fall of Alsoor*"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c3b0f228-6b06-4426-a557-1225d547b908.jpg?1783947926"
    }
}
