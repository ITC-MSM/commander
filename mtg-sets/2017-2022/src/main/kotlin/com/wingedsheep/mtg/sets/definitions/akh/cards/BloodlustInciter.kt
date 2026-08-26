package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodlust Inciter
 * {R}
 * Creature — Human Warrior
 * 1/1
 * {T}: Target creature gains haste until end of turn. (It can attack and {T} this turn.)
 */
val BloodlustInciter = card("Bloodlust Inciter") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    oracleText = "{T}: Target creature gains haste until end of turn. (It can attack and {T} this turn.)"
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.HASTE, creature)
        description = "{T}: Target creature gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "Anthony Palumbo"
        flavorText = "\"To victory! To glory! To eternity!\""
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b1cd0757-1b06-4e1b-a236-31271bd0d9a3.jpg?1783936494"
    }
}
