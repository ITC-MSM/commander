package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vulshok Replica
 * {3}
 * Artifact Creature — Berserker
 * 3/1
 *
 * {1}{R}, Sacrifice this creature: It deals 3 damage to target player or planeswalker.
 */
val VulshokReplica = card("Vulshok Replica") {
    manaCost = "{3}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Berserker"
    power = 3
    toughness = 1
    oracleText = "{1}{R}, Sacrifice this creature: It deals 3 damage to target player or planeswalker."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.SacrificeSelf)
        val t = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(3, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "221"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "All the fury of the Vulshok with only a trace of their recklessness."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32885a6c-b293-405f-9f2e-9e0dd7d1cb8c.jpg?1783941691"
    }
}
