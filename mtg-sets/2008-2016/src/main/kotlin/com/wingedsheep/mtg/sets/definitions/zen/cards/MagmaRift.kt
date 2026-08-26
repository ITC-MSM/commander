package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Magma Rift
 * {2}{R}
 * Sorcery
 * As an additional cost to cast this spell, sacrifice a land.
 * Magma Rift deals 5 damage to target creature.
 *
 * The land sacrifice is an additional *cost* (CR 601.2f), paid on casting — it happens even if
 * the spell is later countered or its target becomes illegal.
 */
val MagmaRift = card("Magma Rift") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice a land.\n" +
        "Magma Rift deals 5 damage to target creature."

    additionalCost(Costs.additional.SacrificePermanent(filter = GameObjectFilter.Land))

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.DealDamage(5, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "136"
        artist = "Jung Park"
        flavorText = "\"Lighting a fire needs kindling and heat. You be the kindling. I'll bring the heat.\"\n—Chandra Nalaar"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee526306-6959-421c-9969-3c19b666d6da.jpg"
    }
}
