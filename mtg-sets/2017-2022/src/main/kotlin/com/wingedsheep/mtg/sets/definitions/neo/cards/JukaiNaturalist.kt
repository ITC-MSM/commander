package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Jukai Naturalist — Kamigawa: Neon Dynasty #225 (canonical printing)
 * {G}{W} · Enchantment Creature — Human Monk · 2/2
 *
 * Lifelink
 * Enchantment spells you cast cost {1} less to cast.
 *
 * The enchantment half of the same reduction [EnthusiasticMechanaut] runs over artifacts — NEO
 * printed the pair as parallel gold uncommons.
 */
val JukaiNaturalist = card("Jukai Naturalist") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Enchantment Creature — Human Monk"
    power = 2
    toughness = 2
    oracleText = "Lifelink\nEnchantment spells you cast cost {1} less to cast."

    keywords(Keyword.LIFELINK)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Enchantment),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Anna Steinbauer"
        flavorText = "He had heard rumors of the cities—the constant noise, the stagnant air, the " +
            "tainted water—and he had no wish to learn if they were true."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5366900b-2abf-4e5c-8507-f51aca9c7ce8.jpg?1783923833"
    }
}
