package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Silent Dart
 * {1}
 * Artifact
 * {4}, {T}, Sacrifice this artifact: It deals 3 damage to target creature.
 */
val SilentDart = card("Silent Dart") {
    manaCost = "{1}"
    typeLine = "Artifact"
    oracleText = "{4}, {T}, Sacrifice this artifact: It deals 3 damage to target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap, Costs.SacrificeSelf)
        val creature = target("target", Targets.Creature)
        effect = Effects.DealDamage(3, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "241"
        artist = "Yeong-Hao Han"
        flavorText = "\"These terms are acceptable to House Dimir. Shall we shake on it?\""
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3af00fdd-6869-4080-8a75-66c8fceeb7de.jpg?1783934106"
    }
}
