package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Nature's Chant — Modern Horizons #210
 * {1}{G/W} · Instant
 *
 * Destroy target artifact or enchantment.
 *
 * A hybrid-cost Naturalize: the "artifact or enchantment" target is one filter with a single
 * `or`-ed card predicate, so both halves share the (default) battlefield scope.
 */
val NaturesChant = card("Nature's Chant") {
    manaCost = "{1}{G/W}"
    colorIdentity = "GW"
    typeLine = "Instant"
    oracleText = "Destroy target artifact or enchantment."

    spell {
        val t = target(
            "target",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Artifact or GameObjectFilter.Enchantment))
        )
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "210"
        artist = "Raoul Vitale"
        flavorText = "\"Plant every sword. Embrace every soul.\"\n—Trostani"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a17cbf6-3f38-4bc6-8448-881e19cffe06.jpg?1783933080"
    }
}
