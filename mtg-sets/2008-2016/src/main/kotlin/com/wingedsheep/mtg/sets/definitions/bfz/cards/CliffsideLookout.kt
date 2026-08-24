package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cliffside Lookout
 * {W}
 * Creature — Kor Scout Ally
 * 1/1
 * {4}{W}: Creatures you control get +1/+1 until end of turn.
 */
val CliffsideLookout = card("Cliffside Lookout") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Scout Ally"
    power = 1
    toughness = 1
    oracleText = "{4}{W}: Creatures you control get +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{4}{W}")
        effect = Patterns.Group.modifyStatsForAll(1, 1, Filters.Group.creaturesYouControl)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Eric Deschamps"
        flavorText = "Though losses run high among the scouts of the Stone Havens, they never flinch from their " +
            "duty."
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c755ae6-badc-4dc8-bfa6-fdebcb00bfba.jpg?1783938221"
    }
}
