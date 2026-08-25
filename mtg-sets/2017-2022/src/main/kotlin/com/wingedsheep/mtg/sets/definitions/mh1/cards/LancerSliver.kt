package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Lancer Sliver
 * {2}{W}
 * Creature — Sliver
 * 2/2
 * Sliver creatures you control have first strike.
 *
 * The keyword-granting half of the Sliver lord shape — [GrantKeyword] over the same
 * "Sliver creatures you control" [GroupFilter] that [CleavingSliver] pumps.
 */
val LancerSliver = card("Lancer Sliver") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control have first strike."

    staticAbility {
        ability = GrantKeyword(
            Keyword.FIRST_STRIKE,
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Lucas Graciano"
        flavorText = "\"These polearms were supposed to help!\"\n—Merrik Aidar, Benalish patrol"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a4f8d9a-3760-449e-b8a6-72b2a641ff23.jpg?1783933160"
    }
}
