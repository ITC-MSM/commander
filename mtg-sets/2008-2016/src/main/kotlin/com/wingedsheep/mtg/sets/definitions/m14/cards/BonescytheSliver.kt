package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bonescythe Sliver
 * {3}{W}
 * Creature — Sliver
 * 2 / 2
 * Sliver creatures you control have double strike. (They deal both first-strike and regular combat damage.)
 */
val BonescytheSliver = card("Bonescythe Sliver") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control have double strike. (They deal both first-strike and regular combat damage.)"

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.DOUBLE_STRIKE,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "9"
        artist = "Trevor Claxton"
        flavorText = "\"Their appendages are sharper than our swords and quicker than our bows.\"\n" +
            "—Hastric, Thunian scout"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a26bb68b-1830-470a-8cea-91edc7db0c57.jpg"
    }
}
