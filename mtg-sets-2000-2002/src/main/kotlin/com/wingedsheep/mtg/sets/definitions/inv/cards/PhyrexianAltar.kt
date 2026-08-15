package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Phyrexian Altar
 * {3}
 * Artifact
 * Sacrifice a creature: Add one mana of any color.
 *
 * The sacrifice cost doesn't disqualify this from being a mana ability: it has
 * no target, isn't a loyalty ability, and could add mana (CR 605.1a).
 */
val PhyrexianAltar = card("Phyrexian Altar") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "Sacrifice a creature: Add one mana of any color."

    activatedAbility {
        cost = Costs.Sacrifice(Filters.Creature)
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "306"
        artist = "Ron Spears"
        imageUri = "https://cards.scryfall.io/normal/front/2/5/25158cd5-749b-408c-9ab1-0f83e38730f7.jpg?1562902485"
    }
}
