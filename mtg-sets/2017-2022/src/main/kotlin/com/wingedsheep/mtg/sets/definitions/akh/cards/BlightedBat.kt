package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blighted Bat
 * {2}{B}
 * Creature — Zombie Bat
 * 2/1
 *
 * Flying
 * {1}: This creature gains haste until end of turn.
 */
val BlightedBat = card("Blighted Bat") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Bat"
    oracleText = "Flying\n{1}: This creature gains haste until end of turn."
    power = 2
    toughness = 1

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
        description = "{1}: This creature gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Nils Hamm"
        flavorText = "Amonkhet's dual suns don't allow for the darkness of night, so bats are most active under the gloom of the frequent sandstorms."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8da308e9-1862-46c1-a62a-90720b484d91.jpg?1783936512"
    }
}
