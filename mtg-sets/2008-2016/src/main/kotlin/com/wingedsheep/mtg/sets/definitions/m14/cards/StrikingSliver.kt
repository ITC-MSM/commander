package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Striking Sliver
 * {R}
 * Creature — Sliver
 * 1 / 1
 * Sliver creatures you control have first strike. (They deal combat damage before creatures without first strike.)
 */
val StrikingSliver = card("Striking Sliver") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "Sliver creatures you control have first strike. (They deal combat damage before creatures without first strike.)"

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.FIRST_STRIKE,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "157"
        artist = "Maciej Kuciara"
        flavorText = "You're too busy recoiling in fear to realize that it's already hit you."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4ee9254b-3d98-4477-a82e-1450cf3ee96e.jpg"
    }
}
