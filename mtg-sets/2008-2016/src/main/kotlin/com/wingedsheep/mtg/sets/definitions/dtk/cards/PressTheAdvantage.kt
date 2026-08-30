package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Press the Advantage
 * {2}{G}{G}
 * Instant
 *
 * Up to two target creatures each get +2/+2 and gain trample until end of turn.
 *
 * "Up to two target creatures **each**" is a `count = 2` requirement, and a `target()` handle on a
 * multi-target requirement resolves to nothing — the bound variable names the requirement, not one
 * chosen target. The corpus shape is [ForEachTargetEffect] over `ContextTarget(0)`: the loop rebinds
 * the context to one chosen target per iteration, so the pump and the grant both land on each
 * creature. The declared requirement is therefore not captured; only its registration matters.
 */
val PressTheAdvantage = card("Press the Advantage") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Up to two target creatures each get +2/+2 and gain trample until end of turn."

    spell {
        target("target", Targets.UpToCreatures(2))
        effect = ForEachTargetEffect(
            listOf(
                Effects.ModifyStats(2, 2, EffectTarget.ContextTarget(0)),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.ContextTarget(0))
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "196"
        artist = "Marco Nelor"
        flavorText = "\"Show your enemies as much mercy as they would show you.\"\n—Surrak, the Hunt Caller"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/09253b39-8b3e-4cb8-b054-0e1a49762387.jpg?1783938578"
    }
}
