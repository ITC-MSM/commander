package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Torpid Moloch
 * {R}
 * Creature — Lizard
 * 3/2
 * Defender (This creature can't attack.)
 * Sacrifice three lands: This creature loses defender until end of turn.
 *
 * "Loses defender" is [Effects.RemoveKeyword], a layer-6 removal lasting until end of turn —
 * activating twice in a turn is redundant, not cumulative.
 */
val TorpidMoloch = card("Torpid Moloch") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard"
    oracleText = "Defender (This creature can't attack.)\n" +
        "Sacrifice three lands: This creature loses defender until end of turn."
    power = 3
    toughness = 2

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.SacrificeMultiple(3, GameObjectFilter.Land)
        effect = Effects.RemoveKeyword(Keyword.DEFENDER, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Paolo Parente"
        flavorText = "Market hucksters sell molochs for use as watchdogs. Of course, that's all molochs do. Sit around . . . and watch."
        imageUri = "https://cards.scryfall.io/normal/front/7/9/7900ff91-0e47-4903-a680-9031f4a23cb4.jpg"
    }
}
