package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Reinforced Bulwark
 * {3}
 * Artifact Creature — Wall
 * 0 / 4
 *
 * Defender
 * {T}: Prevent the next 1 damage that would be dealt to you this turn.
 *
 * Modeling notes:
 *  - `Effects.PreventNextDamage(1, EffectTarget.Controller)` is the capacity shield: an `amount` of
 *    1 with the unified prevention effect's default `Duration.EndOfTurn` and default
 *    `PreventionSourceFilter.AnySource`. The same shape as Shield of the Ages.
 *  - "dealt to **you**" is untargeted — the shield goes on the ability's controller, not on a
 *    chosen player — so `EffectTarget.Controller` and no `target(...)` requirement.
 *  - Not combat-only: the printed line says "damage", so `PreventionScope.AllDamage` (the default)
 *    is right — a burn spell aimed at you is soaked too.
 *  - The `{T}` cost is `Costs.Tap`; DEFENDER is declared through `keywords(...)`, which the engine
 *    reads for the can't-attack restriction as well as for the printed line.
 */
val ReinforcedBulwark = card("Reinforced Bulwark") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Wall"
    power = 0
    toughness = 4
    oracleText = "Defender\n" +
            "{T}: Prevent the next 1 damage that would be dealt to you this turn."

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.PreventNextDamage(1, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "223"
        artist = "Ryan Pancoast"
        flavorText = "Built of wood and iron. Held together by hope and prayer."
        imageUri = "https://cards.scryfall.io/normal/front/4/3/437a91e7-f98e-4ed8-9ab7-f4db62979f5b.jpg?1783941955"
    }
}
