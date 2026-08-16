package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Flowstone Kavu
 * {2}{R}
 * Creature — Kavu
 * 2/3
 * Menace (This creature can't be blocked except by two or more creatures.)
 * {R}: This creature gets +1/-1 until end of turn.
 */
val FlowstoneKavu = card("Flowstone Kavu") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Kavu"
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n{R}: This creature gets +1/-1 until end of turn."
    power = 2
    toughness = 3

    keywords(Keyword.MENACE)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, -1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "125"
        artist = "Simon Dominic"
        flavorText = "The kavu adjusted easily to the Rathi overlay, incorporating the invasive flowstone into their own adaptable forms."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73e40916-3502-448a-a509-f6a6ff3cd73d.jpg?1783921317"
    }
}
