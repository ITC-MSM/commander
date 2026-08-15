package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Gigantoad
 * {3}{G}
 * Creature — Frog
 * 4/4
 *
 * As long as you control seven or more lands, this creature gets +2/+2.
 *
 * Continuous static modification gated by [Conditions.YouControlAtLeast] over the land filter,
 * recomputed at projection.
 */
val Gigantoad = card("Gigantoad") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Frog"
    power = 4
    toughness = 4
    oracleText = "As long as you control seven or more lands, this creature gets +2/+2."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 2, filter = GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(7, GameObjectFilter.Land),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "187"
        artist = "Hristo D. Chukov"
        flavorText = "The gigantoad is known to venture outside of its comfort zone during downpours" +
            "—much to the surprise of unsuspecting hikers."
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc10d648-4053-460f-bc52-9c20477bf6de.jpg?1748706462"
    }
}
