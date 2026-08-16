package com.wingedsheep.mtg.sets.definitions.hou.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Overcome
 * {3}{G}{G}
 * Sorcery
 * Creatures you control get +2/+2 and gain trample until end of turn. (Each of those creatures can deal excess combat damage to the player or planeswalker it's attacking.)
 *
 * Overrun's shape at one less power: one group named once, two things said about it, so it is
 * [Patterns.Group.pumpAndGrantToAll] rather than a pump composed with a keyword grant — the group is
 * gathered a single time, on resolution, which is exactly the "only creatures you control at the time
 * it resolves" ruling.
 */
val Overcome = card("Overcome") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Creatures you control get +2/+2 and gain trample until end of turn. (Each of those creatures can deal excess combat damage to the player or planeswalker it's attacking.)"

    spell {
        effect = Patterns.Group.pumpAndGrantToAll(
            power = 2,
            toughness = 2,
            keyword = Keyword.TRAMPLE,
            filter = Filters.Group.creaturesYouControl,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "125"
        artist = "Craig J Spearing"
        flavorText = "\"Forward! Until the horizon is ours!\" —Khemses, charioteer"
        imageUri = "https://cards.scryfall.io/normal/front/d/1/d1dc2427-685a-4739-8b92-e60f134d4adb.jpg?1783936018"

        ruling(
            "2019-07-12",
            "Overcome affects only creatures you control at the time it resolves. Creatures you " +
                "begin to control later in the turn won't get +2/+2 or gain trample."
        )
    }
}
