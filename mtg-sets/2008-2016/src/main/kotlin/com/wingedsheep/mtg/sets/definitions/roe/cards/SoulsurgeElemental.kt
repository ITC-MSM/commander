package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soulsurge Elemental
 * {3}{R}
 * Creature — Elemental
 * \* / 1
 *
 * First strike
 * Soulsurge Elemental's power is equal to the number of creatures you control.
 *
 * Modeling notes:
 *  - The printed power is a `*`, so power is a characteristic-defining ability (CR 604.3): a
 *    Layer 7a value recomputed continuously at projection, not a one-shot pump. `dynamicPower(...)`
 *    is that CDA, and the card emits **no** `power =` line — a printed base would be the thing the
 *    CDA is supposed to replace.
 *  - Only power is dynamic; toughness is a printed 1. That is exactly the split the single-stat
 *    `dynamicPower(source)` helper exists for, so this uses it rather than the both-stats
 *    `dynamicStats(...)` (same reasoning as Kraven, Proud Predator).
 *  - "the number of creatures you control" is `DynamicAmounts.creaturesYouControl()`, i.e.
 *    `battlefield(Player.You, GameObjectFilter.Creature).count()`. The Elemental is itself a
 *    creature you control, so it counts itself — a lone Soulsurge Elemental is a 1/1, which is
 *    what the card does.
 *  - First strike is engine-live, so a plain `keywords(...)` declaration is the whole of it.
 */
val SoulsurgeElemental = card("Soulsurge Elemental") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    toughness = 1
    dynamicPower(DynamicAmounts.creaturesYouControl())
    oracleText = "First strike\n" +
            "Soulsurge Elemental's power is equal to the number of creatures you control."

    keywords(Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "163"
        artist = "Jason A. Engle"
        flavorText = "It draws its strength from those around it, but the pleasure of destruction is all its own."
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d0dcb81b-dcdb-44ca-9ef5-0b45d276c0dd.jpg?1783941971"
    }
}
