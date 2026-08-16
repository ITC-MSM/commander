package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Caustic Caterpillar
 * {G}
 * Creature — Insect
 * 1/1
 * {1}{G}, Sacrifice this creature: Destroy target artifact or enchantment.
 */
val CausticCaterpillar = card("Caustic Caterpillar") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 1
    toughness = 1
    oracleText = "{1}{G}, Sacrifice this creature: Destroy target artifact or enchantment."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}"), Costs.SacrificeSelf)
        val t = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Jack Wang"
        flavorText = "\"The rare and beautiful butterflies inspire the design of our thopters. The larvae, however, are a different story entirely.\" —Kiran Nalaar, Ghirapur inventor"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f03d4e0d-bddd-4835-91b8-11c2f15e54c3.jpg?1783938324"
    }
}
