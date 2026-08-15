package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Shivan Harvest
 * {1}{R}
 * Enchantment
 * {1}{R}, Sacrifice a creature: Destroy target nonbasic land.
 */
val ShivanHarvest = card("Shivan Harvest") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "{1}{R}, Sacrifice a creature: Destroy target nonbasic land."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.Sacrifice(Filters.Creature))
        target = TargetPermanent(
            filter = TargetFilter(
                GameObjectFilter(
                    cardPredicates = listOf(
                        CardPredicate.IsLand,
                        CardPredicate.Not(CardPredicate.IsBasicLand),
                    )
                )
            )
        )
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
        description = "{1}{R}, Sacrifice a creature: Destroy target nonbasic land."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "167"
        artist = "Daren Bader"
        flavorText = "\"The blood of your ancestors ran heavy on this soil. Now it's your turn to sacrifice for the glory of Shiv.\"\n—Viashino heretic"
        imageUri = "https://cards.scryfall.io/normal/front/4/7/47dbd765-d7ea-4181-bd22-5c749ad081af.jpg?1562909648"
    }
}
