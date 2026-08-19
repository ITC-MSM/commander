package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fire Drake
 * {1}{R}{R}
 * Creature — Drake
 * 1/2
 * Flying
 * {R}: This creature gets +1/+0 until end of turn. Activate only once each turn.
 */
val FireDrake = card("Fire Drake") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Drake"
    power = 1
    toughness = 2
    oracleText = "Flying\n{R}: This creature gets +1/+0 until end of turn. Activate only once each turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{R}")
        restrictions = listOf(ActivationRestriction.OncePerTurn)
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Christopher Rush"
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3419db6-1c38-4aa4-b953-1dde7d22b927.jpg?1783947935"
    }
}
