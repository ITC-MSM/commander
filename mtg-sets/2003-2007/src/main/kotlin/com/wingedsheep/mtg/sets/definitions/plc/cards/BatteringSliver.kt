package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Battering Sliver
 * {5}{R}
 * Creature — Sliver
 * 4/4
 * All Sliver creatures have trample.
 *
 * The pre-Future Sight Sliver templating: "**All** Sliver creatures", with no "you control"
 * clause, so the [GroupFilter] carries no controller predicate and the grant reaches every
 * player's Slivers.
 */
val BatteringSliver = card("Battering Sliver") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Sliver"
    power = 4
    toughness = 4
    oracleText = "All Sliver creatures have trample."

    staticAbility {
        ability = GrantKeyword(
            Keyword.TRAMPLE,
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Greg Staples"
        flavorText = "Covered with hard shell-like plates, these slivers burrow through solid rock to carve out new nests for their hives."
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0dc86d6-aa3e-46c6-9405-86f1e1ee7844.jpg"
    }
}
