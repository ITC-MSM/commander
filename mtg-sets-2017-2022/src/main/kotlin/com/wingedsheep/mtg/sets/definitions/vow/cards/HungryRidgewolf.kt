package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Hungry Ridgewolf
 * {1}{R}
 * Creature — Wolf
 * 2/2
 * As long as you control another Wolf or Werewolf, this creature gets +1/+0 and has trample.
 *
 * The pump and trample share one "as long as" condition, split across two continuous static
 * abilities (stats + keyword). The condition checks for another Wolf-or-Werewolf you control
 * (excludeSelf, so this creature doesn't count itself).
 */
val HungryRidgewolf = card("Hungry Ridgewolf") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wolf"
    power = 2
    toughness = 2
    oracleText = "As long as you control another Wolf or Werewolf, this creature gets +1/+0 and has trample."

    val anotherWolfOrWerewolf = Conditions.YouControl(
        GameObjectFilter.Creature.withSubtype("Wolf") or GameObjectFilter.Creature.withSubtype("Werewolf"),
        excludeSelf = true,
    )

    staticAbility {
        ability = ModifyStats(1, 0, Filters.Self)
        condition = anotherWolfOrWerewolf
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter.source())
        condition = anotherWolfOrWerewolf
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "161"
        artist = "Olena Richards"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/804e0e1d-9c9c-46e6-8533-88e18a91ceff.jpg?1782703075"
    }
}
