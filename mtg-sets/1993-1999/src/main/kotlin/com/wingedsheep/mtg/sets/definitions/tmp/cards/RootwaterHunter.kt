package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rootwater Hunter
 * {2}{U}
 * Creature — Merfolk
 * 1/1
 * {T}: This creature deals 1 damage to any target.
 */
val RootwaterHunter = card("Rootwater Hunter") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk"
    power = 1
    toughness = 1
    oracleText = "{T}: This creature deals 1 damage to any target."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
        description = "{T}: This creature deals 1 damage to any target."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Brom"
        flavorText = "\"Bitter water vicious wave\n" +
            "Shadow-cold shallows root-made maze\n" +
            "Home's angry embrace.\"\n" +
            "—*Rootwater Saga*"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdf7ea34-2cde-4ec5-9b12-99b0002da986.jpg"
    }
}
