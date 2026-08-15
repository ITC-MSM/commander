package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Hooded Kavu
 * {2}{R}
 * Creature — Kavu
 * 2/2
 * {B}: This creature gains fear until end of turn.
 */
val HoodedKavu = card("Hooded Kavu") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu"
    power = 2
    toughness = 2
    oracleText = "{B}: This creature gains fear until end of turn. " +
        "(It can't be blocked except by artifact creatures and/or black creatures.)"

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.GrantKeyword(Keyword.FEAR, EffectTarget.Self)
        description = "{B}: This creature gains fear until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "John Howe"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/5464b80a-22fe-42c7-a839-31667712fb2d.jpg?1562912199"
    }
}
