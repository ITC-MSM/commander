package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Belligerent Sliver
 * {2}{R}
 * Creature — Sliver
 * 2/2
 * Sliver creatures you control have menace.
 *
 * A Magic 2015 Sliver: unlike the Tempest cycle it lords over *your* Slivers only, so the group
 * filter carries `youControl()`.
 */
val BelligerentSliver = card("Belligerent Sliver") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control have menace. (They can't be blocked except by two or more creatures.)"

    staticAbility {
        ability = GrantKeyword(
            Keyword.MENACE,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver").youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "129"
        artist = "Raymond Swanland"
        flavorText = "\"The slivers became adept at provoking a fear response in other species.\"\n—Hastric, Thunian scout"
        imageUri = "https://cards.scryfall.io/normal/front/b/7/b78b8268-b090-4012-a3ba-5daab491f78d.jpg?1783939177"
    }
}
