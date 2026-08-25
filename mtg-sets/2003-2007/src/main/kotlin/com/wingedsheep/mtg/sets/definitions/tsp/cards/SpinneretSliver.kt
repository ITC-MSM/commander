package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Spinneret Sliver
 * {1}{G}
 * Creature — Sliver
 * 2/2
 * All Sliver creatures have reach.
 */
val SpinneretSliver = card("Spinneret Sliver") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Sliver creatures have reach."

    staticAbility {
        ability = GrantKeyword(
            Keyword.REACH,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "219"
        artist = "Michael Sutfin"
        flavorText = "Each new generation of slivers evolves to assimilate the strengths of the prey upon which their progenitors fed."
        imageUri = "https://cards.scryfall.io/normal/front/d/a/da698c63-f167-4129-a650-b50c080a24b5.jpg"
    }
}
