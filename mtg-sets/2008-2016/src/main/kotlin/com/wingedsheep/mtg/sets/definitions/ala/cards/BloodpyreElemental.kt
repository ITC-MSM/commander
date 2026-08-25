package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Bloodpyre Elemental
 * {4}{R}
 * Creature — Elemental
 * 4 / 1
 * Sacrifice this creature: It deals 4 damage to target creature. Activate only as a sorcery.
 *
 * [Costs.SacrificeSelf] is the whole cost, so the Elemental is already gone by the time the ability
 * resolves — "it deals 4 damage" is the ability's source dealing it, which is
 * [Effects.DealDamage]'s default source, so no explicit `damageSource` is needed. "Activate only
 * as a sorcery" is the ability's [TimingRule.SorcerySpeed], a timing rule rather than an
 * activation restriction.
 */
val BloodpyreElemental = card("Bloodpyre Elemental") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 1
    oracleText = "Sacrifice this creature: It deals 4 damage to target creature. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.SacrificeSelf
        val creature = target("target", Targets.Creature)
        effect = Effects.DealDamage(4, creature)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Trevor Claxton"
        flavorText = "Elementals born of Jund are as cruel and unstable as the plane itself."
        imageUri = "https://cards.scryfall.io/normal/front/8/9/89e39677-5e04-45af-8f4c-1d4b6e737213.jpg"
    }
}
