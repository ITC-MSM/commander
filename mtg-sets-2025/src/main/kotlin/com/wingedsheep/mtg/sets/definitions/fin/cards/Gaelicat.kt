package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Gaelicat
 * {2}{W}
 * Creature — Cat
 * 1/3
 *
 * Flying, vigilance
 * As long as you control two or more artifacts, this creature gets +2/+0.
 *
 * The conditional buff is a continuous static modification gated by
 * [Conditions.YouControlAtLeast] over the artifact filter, recomputed at projection.
 */
val Gaelicat = card("Gaelicat") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat"
    power = 1
    toughness = 3
    oracleText = "Flying, vigilance\nAs long as you control two or more artifacts, this creature gets +2/+0."

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 2, toughnessBonus = 0, filter = GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(2, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Narendra Bintara Adi"
        flavorText = "\"That's the ticket! Now, let's get moving to North Mountain!\"\n—Galuf Baldesion"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29606c49-e1a4-49c3-883b-9122c08bbbc7.jpg?1748705839"
    }
}
