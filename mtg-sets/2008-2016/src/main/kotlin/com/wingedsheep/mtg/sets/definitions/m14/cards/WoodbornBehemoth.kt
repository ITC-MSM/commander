package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Woodborn Behemoth
 * {3}{G}{G}
 * Creature — Elemental
 * 4/4
 * As long as you control eight or more lands, this creature gets +4/+4 and has trample.
 *
 * The Earth Rumble Wrestlers shape: one "as long as" condition
 * ([Conditions.ControlLandsAtLeast]`(8)`) shared by two continuous statics — [ModifyStats] in Layer
 * 7c and [GrantKeyword]`(TRAMPLE)` in Layer 6 — both scoped to the source permanent.
 */
val WoodbornBehemoth = card("Woodborn Behemoth") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    power = 4
    toughness = 4
    oracleText = "As long as you control eight or more lands, this creature gets +4/+4 and has trample. (It can deal excess combat damage to the player or planeswalker it's attacking.)"

    val eightLands = Conditions.ControlLandsAtLeast(8)

    staticAbility {
        ability = ModifyStats(4, 4, Filters.Self)
        condition = eightLands
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter.source())
        condition = eightLands
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "203"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c73dbf3-e68e-4f21-b6ca-94302bf5574c.jpg?1783939898"
    }
}
