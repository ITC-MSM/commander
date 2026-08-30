package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Blur Sliver
 * {2}{R}
 * Creature — Sliver
 * 2 / 2
 * Sliver creatures you control have haste. (They can attack and {T} as soon as they come under your control.)
 */
val BlurSliver = card("Blur Sliver") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control have haste. (They can attack and {T} as soon as they come under your control.)"

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.HASTE,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Daarken"
        flavorText = "They move in a synchronized swarm, turning entire squads into heaps of bloody rags and bones in an instant."
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63227937-86cc-45e0-9e9e-8c7ab80cbaef.jpg"
    }
}
