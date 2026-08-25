package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Kobold Overlord
 * {1}{R}
 * Creature — Kobold
 * 1/2
 *
 * First strike
 * Other Kobold creatures you control have first strike.
 */
val KoboldOverlord = card("Kobold Overlord") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kobold"
    power = 1
    toughness = 2
    oracleText = "First strike\nOther Kobold creatures you control have first strike."

    keywords(Keyword.FIRST_STRIKE)
    staticAbility {
        ability = GrantKeyword(
            Keyword.FIRST_STRIKE,
            GroupFilter(
                GameObjectFilter.Creature.withSubtype(Subtype.KOBOLD).youControl(),
                excludeSelf = true,
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "155"
        artist = "Julie Baroh"
        flavorText = "\"One for all, all for one; we strike first, and then you're done!\" —Oath of the Kobold " +
            "Musketeers"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/490eeedb-9c03-4dc7-81fd-ae54a7932e4d.jpg?1783948054"
    }
}
