package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Shadow Sliver
 * {2}{U}
 * Creature — Sliver
 * 1/1
 * All Sliver creatures have shadow. (They can block or be blocked by only creatures with shadow.)
 */
val ShadowSliver = card("Shadow Sliver") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Sliver creatures have shadow. (They can block or be blocked by only creatures with shadow.)"

    staticAbility {
        ability = GrantKeyword(
            Keyword.SHADOW,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Warren Mahy"
        flavorText = "These slivers, trapped between worlds since the Rathi overlay, are among the last to claim direct lineage from the lost Sliver Queen."
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb725914-1b0f-4efc-808b-9fe2eaa7f17d.jpg"
    }
}
