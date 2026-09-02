package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Scurrid Colony — Strixhaven: School of Mages #142 (canonical printing)
 * {1}{G} · Creature — Squirrel · 2/2
 *
 * Reach
 * This creature gets +2/+2 as long as you control eight or more lands.
 *
 * The Woodborn Behemoth shape: one self-scoped [ModifyStats] static in Layer 7c, gated by
 * [Conditions.ControlLandsAtLeast]`(8)` — the builder wraps the pair into a conditional static.
 */
val ScurridColony = card("Scurrid Colony") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Squirrel"
    oracleText =
        "Reach\n" +
        "This creature gets +2/+2 as long as you control eight or more lands."
    power = 2
    toughness = 2

    keywords(Keyword.REACH)

    staticAbility {
        ability = ModifyStats(2, 2, Filters.Self)
        condition = Conditions.ControlLandsAtLeast(8)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Lars Grant-West"
        flavorText = "Scurrid teeth evolved to crack open nuts, but they work just as well for crushing bones if the nest is threatened."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c9c0a096-870d-443c-92d3-f7ec1f362616.jpg?1783927338"
    }
}
