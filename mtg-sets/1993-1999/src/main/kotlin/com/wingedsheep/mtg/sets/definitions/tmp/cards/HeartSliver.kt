package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Heart Sliver
 * {1}{R}
 * Creature — Sliver
 * 1/1
 * All Sliver creatures have haste.
 */
val HeartSliver = card("Heart Sliver") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Sliver creatures have haste."

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver")))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "182"
        artist = "Ron Spencer"
        flavorText = "Gerrard looked all around for the source of the mysterious pulse, and at that moment the slivers boiled forth from the crevasses."
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27a83ab6-0d15-49e4-90e3-b3a2a095c632.jpg"
    }
}
