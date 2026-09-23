package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Budding Insurgent — "If that permanent was a legendary enchantment" is checked after the destroy,
 * against the target as it last existed; the draw doesn't depend on the permanent actually being
 * destroyed (an indestructible legendary enchantment still draws), only on the target still being
 * legal when the ability resolves.
 */
val BuddingInsurgent = card("Budding Insurgent") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dryad Scout"
    power = 3
    toughness = 3
    oracleText = "Vigilance\n" +
        "Sacrifice this creature: Destroy target artifact or enchantment. If that permanent was a " +
        "legendary enchantment, draw a card. Activate only as a sorcery."

    keywords(Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.SacrificeSelf
        val permanent = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Composite(
            Effects.Destroy(permanent),
            ConditionalEffect(
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Enchantment.legendary()),
                effect = Effects.DrawCards(1),
            ),
        )
        timing = TimingRule.SorcerySpeed
        description = "Destroy target artifact or enchantment. If that permanent was a legendary " +
            "enchantment, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Jeff Miracola"
        flavorText = "\"If we're going to fix things, we should start here. Why does no one question the Theorist?\""
        imageUri = "https://cards.scryfall.io/normal/front/1/8/18c59d60-2640-4576-9375-3ba38aa3ecb7.jpg?1789127120"
        inBooster = false
    }
}
