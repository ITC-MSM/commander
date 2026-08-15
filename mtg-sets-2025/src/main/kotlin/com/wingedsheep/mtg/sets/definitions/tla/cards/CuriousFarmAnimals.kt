package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Curious Farm Animals
 * {W}
 * Creature — Boar Elk Bird Ox
 * 1/1
 * When this creature dies, you gain 3 life.
 * {2}, Sacrifice this creature: Destroy up to one target artifact or enchantment.
 */
val CuriousFarmAnimals = card("Curious Farm Animals") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Boar Elk Bird Ox"
    power = 1
    toughness = 1
    oracleText = "When this creature dies, you gain 3 life.\n" +
        "{2}, Sacrifice this creature: Destroy up to one target artifact or enchantment."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.GainLife(3)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeSelf)
        val t = target(
            "artifact or enchantment",
            TargetPermanent(optional = true, filter = TargetFilter.ArtifactOrEnchantment),
        )
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "John Di Giovanni"
        flavorText = "Earth Kingdom cuisine offers a stunning array of unique meats, " +
            "like chicken-pork, deer-pork, and beef-pork."
        imageUri = "https://cards.scryfall.io/normal/front/2/4/2402d759-84b6-41d2-ad78-9333974e9222.jpg?1764119964"
    }
}
