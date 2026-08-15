package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless
import com.wingedsheep.sdk.scripting.CantBlockUnless
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Tiger-Dillo
 * {1}{R}
 * Creature — Cat Armadillo
 * 4/3
 *
 * This creature can't attack or block unless you control another creature with power 4 or greater.
 */
val TigerDillo = card("Tiger-Dillo") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cat Armadillo"
    power = 4
    toughness = 3
    oracleText = "This creature can't attack or block unless you control another creature with power 4 or greater."

    // "another creature with power 4 or greater" — exclude this creature itself from the check.
    staticAbility {
        ability = CantAttackUnless(
            Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4), excludeSelf = true)
        )
    }
    staticAbility {
        ability = CantBlockUnless(
            Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4), excludeSelf = true)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "155"
        artist = "John Di Giovanni"
        flavorText = "Predators with armor plating can be as bold or as cowardly as they desire."
        imageUri = "https://cards.scryfall.io/normal/front/0/6/067bd117-04e6-410e-89c4-6d431d627751.jpg?1764121066"
    }
}
