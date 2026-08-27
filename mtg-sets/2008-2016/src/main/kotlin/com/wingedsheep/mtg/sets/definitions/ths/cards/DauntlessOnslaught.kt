package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dauntless Onslaught
 * {2}{W}
 * Instant
 *
 * Up to two target creatures each get +2/+2 until end of turn.
 *
 * "Up to two target creatures **each** get +2/+2" is a plural requirement, so the pump runs
 * once per chosen target ([ForEachTargetEffect] over `ContextTarget(0)`) rather than once
 * against the requirement as a whole.
 */
val DauntlessOnslaught = card("Dauntless Onslaught") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Up to two target creatures each get +2/+2 until end of turn."

    spell {
        target("target", Targets.UpToCreatures(2))
        effect = ForEachTargetEffect(
            listOf(Effects.ModifyStats(2, 2, EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Peter Mohrbacher"
        flavorText = "\"The people of Akros must learn from our leonin adversaries. If we match their staunch ferocity with our superior faith, we cannot fail.\"\n—Cymede, queen of Akros"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e652229d-81fe-4261-bebc-9e405bb2d991.jpg"
    }
}
