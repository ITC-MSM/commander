package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sylvok Replica
 * {3}
 * Artifact Creature — Shaman
 * 1/3
 *
 * {G}, Sacrifice this creature: Destroy target artifact or enchantment.
 */
val SylvokReplica = card("Sylvok Replica") {
    manaCost = "{3}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Shaman"
    power = 1
    toughness = 3
    oracleText = "{G}, Sacrifice this creature: Destroy target artifact or enchantment."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.SacrificeSelf)
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "210"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "All the zeal of the Sylvok with only a trace of their conservancy."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7caa3ce3-15a9-40ca-ad45-baff0f276483.jpg?1783941695"
    }
}
