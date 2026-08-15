package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Viashino Grappler
 * {2}{R}
 * Creature — Lizard
 * 3/1
 * {G}: This creature gains trample until end of turn.
 */
val ViashinoGrappler = card("Viashino Grappler") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard"
    power = 3
    toughness = 1
    oracleText = "{G}: This creature gains trample until end of turn."

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
        description = "{G}: This creature gains trample until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "179"
        artist = "Mark Romanoski"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a94aeb4-349c-4394-848d-c1c9133856e2.jpg?1562910116"
    }
}
