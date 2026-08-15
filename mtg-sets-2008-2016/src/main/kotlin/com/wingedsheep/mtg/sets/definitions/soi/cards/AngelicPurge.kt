package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Angelic Purge
 * {2}{W}
 * Sorcery
 * As an additional cost to cast this spell, sacrifice a permanent.
 * Exile target artifact, creature, or enchantment.
 */
val AngelicPurge = card("Angelic Purge") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice a permanent.\n" +
        "Exile target artifact, creature, or enchantment."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Permanent))

    spell {
        target = TargetObject(filter = TargetFilter.ArtifactCreatureOrEnchantment)
        effect = Effects.Exile(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Zezhou Chen"
        flavorText = "\"We must save you from yourselves.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d38bae3e-95f3-413a-bb4d-6a0814112a7a.jpg?1782712158"
    }
}
