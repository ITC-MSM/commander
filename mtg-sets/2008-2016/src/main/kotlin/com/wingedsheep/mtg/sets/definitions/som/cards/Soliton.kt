package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Soliton
 * {5}
 * Artifact Creature — Construct
 * 3/4
 *
 * {U}: Untap this creature.
 *
 * The untap is repeatable and instant-speed, so the Soliton can attack and then untap to block —
 * or shrug off a tapper for one blue each time.
 */
val Soliton = card("Soliton") {
    manaCost = "{5}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Construct"
    power = 3
    toughness = 4
    oracleText = "{U}: Untap this creature."

    activatedAbility {
        cost = Costs.Mana("{U}")
        effect = Effects.Untap(EffectTarget.Self)
        description = "{U}: Untap this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "204"
        artist = "Jason Felix"
        flavorText = "The gemini engines had lost connection with each other and wandered apart, developing an independent awareness of their surroundings."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7b608c28-18cc-47d6-861e-2fd783aa3ade.jpg?1783941695"
    }
}
