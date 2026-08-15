package com.wingedsheep.mtg.sets.definitions.jud.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Lava Dart {R}
 * Instant
 *
 * Lava Dart deals 1 damage to any target.
 * Flashback—Sacrifice a Mountain. (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 *
 * Modeled as a simple damage spell with [Targets.Any] (any creature, player, or planeswalker)
 * and a flashback ability whose cost is sacrificing a Mountain (no mana cost).
 */
val LavaDart = card("Lava Dart") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Lava Dart deals 1 damage to any target.\nFlashback—Sacrifice a Mountain. (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        target = Targets.Any
        effect = Effects.DealDamage(1, EffectTarget.ContextTarget(0))
    }

    keywordAbility(KeywordAbility.flashback("", Costs.additional.SacrificePermanent(Filters.MountainCard)))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Darrell Riche"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/865bb1d3-5b7d-40e9-87cc-96be9524a105.jpg?1782719236"
    }
}
