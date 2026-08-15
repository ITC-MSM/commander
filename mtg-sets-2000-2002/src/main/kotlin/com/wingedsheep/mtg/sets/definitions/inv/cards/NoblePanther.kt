package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Noble Panther
 * {1}{G}{W}
 * Creature — Cat
 * 3/3
 * {1}: This creature gains first strike until end of turn.
 */
val NoblePanther = card("Noble Panther") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Cat"
    power = 3
    toughness = 3
    oracleText = "{1}: This creature gains first strike until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self, Duration.EndOfTurn)
        description = "{1}: This creature gains first strike until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "257"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3f327818-8222-4295-8cef-118757b34d17.jpg?1562907700"
    }
}
