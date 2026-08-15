package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.LoseAllAbilities
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.TransformPermanent

/**
 * Eaten by Piranhas
 * {1}{U}
 * Enchantment — Aura
 * Uncommon (LCI #54)
 *
 * Flash
 * Enchant creature
 * Enchanted creature loses all abilities and is a black Skeleton creature with base power
 * and toughness 1/1. (It loses all other colors, card types, and creature types.)
 */
val EatenByPiranhas = card("Eaten by Piranhas") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash\nEnchant creature\nEnchanted creature loses all abilities and is a black Skeleton creature with base power and toughness 1/1. (It loses all other colors, card types, and creature types.)"

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    // "loses all abilities" — Layer 6 (ABILITY)
    staticAbility {
        ability = LoseAllAbilities()
    }

    // "is a black Skeleton creature" — Layer 4 (TYPE: replaces card types + subtypes) +
    // Layer 5 (COLOR: replaces all colors with black)
    staticAbility {
        ability = TransformPermanent(
            setCardTypes = setOf("CREATURE"),
            setSubtypes = setOf("Skeleton"),
            setColors = setOf(Color.BLACK)
        )
    }

    // "base power and toughness 1/1" — Layer 7b (POWER_TOUGHNESS, SET_BASE_VALUES)
    staticAbility {
        ability = SetBasePowerToughnessStatic(1, 1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "54"
        artist = "Abz J Harding"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0c504ef-2382-4174-9b1d-5f38e12a28fc.jpg?1782694566"
    }
}
