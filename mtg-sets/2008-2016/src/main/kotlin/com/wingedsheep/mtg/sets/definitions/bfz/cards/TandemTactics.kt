package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tandem Tactics
 * {1}{W}
 * Instant
 * Up to two target creatures each get +1/+2 until end of turn. You gain 2 life.
 *
 * "Up to two target creatures **each** get +1/+2" is a plural requirement, so the pump runs
 * once per chosen target ([ForEachTargetEffect] over `ContextTarget(0)`) rather than once
 * against the requirement as a whole.
 */
val TandemTactics = card("Tandem Tactics") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Up to two target creatures each get +1/+2 until end of turn. You gain 2 life."

    spell {
        target("target creature", Targets.UpToCreatures(2))
        effect = Effects.Composite(
            ForEachTargetEffect(listOf(Effects.ModifyStats(1, 2, EffectTarget.ContextTarget(0)))),
            Effects.GainLife(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "52"
        artist = "David Gaillet"
        flavorText = "In times of infestation and war, Zendikar favors the blades that strike in unison."
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a8aaf9c-9aa5-44db-9610-402389a3ddc5.jpg?1783938214"
    }
}
