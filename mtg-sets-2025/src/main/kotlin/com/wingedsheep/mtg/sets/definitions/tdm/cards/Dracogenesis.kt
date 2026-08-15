package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MayCastWithoutPayingManaCost

/**
 * Dracogenesis — Tarkir: Dragonstorm #105
 * {6}{R}{R} · Enchantment
 *
 * You may cast Dragon spells without paying their mana costs.
 */
val Dracogenesis = card("Dracogenesis") {
    manaCost = "{6}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "You may cast Dragon spells without paying their mana costs."

    staticAbility {
        ability = MayCastWithoutPayingManaCost(
            controllerOnly = true,
            spellFilter = GameObjectFilter.Any.withSubtype("Dragon")
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "105"
        artist = "Kai Carpenter"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d5674f9-22b2-45f9-902d-4fd245485c60.jpg?1743204385"
    }
}
