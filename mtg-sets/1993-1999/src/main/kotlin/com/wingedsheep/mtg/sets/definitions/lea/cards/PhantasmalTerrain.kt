package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.SetEnchantedLandTypeFromChosen

/**
 * Phantasmal Terrain
 * {U}{U}
 * Enchantment — Aura
 * Enchant land
 * As this Aura enters, choose a basic land type.
 * Enchanted land is the chosen type.
 *
 * Canonical [com.wingedsheep.sdk.model.CardDefinition] lives in Alpha (the card's
 * earliest printing); later sets (Invasion, etc.) contribute only `Printing` rows.
 */
val PhantasmalTerrain = card("Phantasmal Terrain") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant land\nAs this Aura enters, choose a basic land type.\nEnchanted land is the chosen type."

    auraTarget = Targets.Land

    replacementEffect(EntersWithChoice(ChoiceType.BASIC_LAND_TYPE))

    staticAbility {
        ability = SetEnchantedLandTypeFromChosen
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Dameon Willich"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c371aa1-1619-41e3-8364-7bc9b8cf5d14.jpg?1559591494"
    }
}
