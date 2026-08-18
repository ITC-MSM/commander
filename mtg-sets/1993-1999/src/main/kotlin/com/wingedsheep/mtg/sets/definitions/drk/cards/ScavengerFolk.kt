package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Scavenger Folk
 * {G}
 * Creature — Human
 * 1/1
 * {G}, {T}, Sacrifice this creature: Destroy target artifact.
 */
val ScavengerFolk = card("Scavenger Folk") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human"
    power = 1
    toughness = 1
    oracleText = "{G}, {T}, Sacrifice this creature: Destroy target artifact."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.Tap, Costs.SacrificeSelf)
        val artifact = target("target artifact", Targets.Artifact)
        effect = Effects.Destroy(artifact)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Dennis Detwiller"
        flavorText = "String, weapons, wax, or jewels—it makes no difference. Leave nothing unguarded in Scarwood."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e99870c-b2b9-431b-b8a8-3f4a80aa8fa5.jpg?1783947929"
    }
}
